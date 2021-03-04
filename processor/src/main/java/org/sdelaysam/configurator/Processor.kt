package org.sdelaysam.configurator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

class Processor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(GenerateViewHolder::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        environment: RoundEnvironment
    ): Boolean {
        val annotatedElements = environment.getElementsAnnotatedWith(GenerateViewHolder::class.java)
        if (annotatedElements.isEmpty()) return false
        if (processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] == null) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Can't find the target directory for generated Kotlin files."
            )
            return false
        }
        return annotatedElements.all(::processAnnotation)
    }

    private fun processAnnotation(element: Element): Boolean {
        val elementUtils = processingEnv.elementUtils
        val typeUtils = processingEnv.typeUtils
        val className = element.simpleName.toString()
        val elementType = element.asType()
        val entryType = elementUtils
            .getTypeElement("org.sdelaysam.configurator.adapter.AdapterEntry")
            .asType()
        if (!typeUtils.isAssignable(elementType, entryType)) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "$className should implement AdapterEntry"
            )
            return false
        }

        val viewTypeElement =
            element.enclosedElements.find { it.simpleName.contentEquals("viewType") }
        val identity = (viewTypeElement as? VariableElement)?.constantValue as? Int
        if (identity == null) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "$className should have constant \"viewType\""
            )
            return false
        }

        val configuratorType = typeUtils.getDeclaredType(
            elementUtils.getTypeElement("org.sdelaysam.configurator.Configurator"),
            typeUtils.getWildcardType(null, null)
        )

        if (!typeUtils.isSubtype(elementType, configuratorType)) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "$className should extend Configurator<T>")
            return false
        }

        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "Processing $className \r\n")

        val androidViewType = elementUtils
            .getTypeElement("android.view.View")
            .asType()

        val viewBindingType = elementUtils
            .getTypeElement("androidx.viewbinding.ViewBinding")
            ?.asType()


        val target = element.getAnnotation(GenerateViewHolder::class.java).type

        var generated = false
        var superclassType = (element as TypeElement).superclass
        loop@ while (superclassType != null && superclassType.kind == TypeKind.DECLARED) {
            val generics = (superclassType as? DeclaredType)?.typeArguments
            if (generics != null) {
                for (type in generics) {
                    if (viewBindingType != null && typeUtils.isAssignable(type, viewBindingType)) {
                        try {
                            val typeElement = (type as DeclaredType).asElement()
                            generated = generateViewBindingHolder(target, element, typeElement, identity)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        break@loop
                    }
                    if (typeUtils.isSubtype(type, androidViewType)) {
                        try {
                            val typeElement = (type as DeclaredType).asElement()
                            generated = generateViewHolder(target, element, typeElement, identity)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        break@loop
                    }
                }
            }
            superclassType =
                ((superclassType as DeclaredType).asElement() as? TypeElement)?.superclass
        }

        if (!generated) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Unable to generate view holder for $className. Only View and ViewBinding factories are supported."
            )
            return false
        }

        return true
    }

    private fun generateViewHolder(
        type: ViewHolderType,
        mediator: Element,
        view: Element,
        viewType: Int
    ): Boolean {
        return generateClass(
            type = type,
            configurator = mediator,
            view = view,
            viewType = viewType,
            superClassInit = {
                addSuperclassConstructorParameter("view")
                addSuperclassConstructorParameter("view")
            },
            factoryInit = {
                val viewClass = view.getClassName()
                addStatement("val view = %T(parent.context)", viewClass)
            }
        )
    }

    private fun generateViewBindingHolder(
        type: ViewHolderType,
        mediator: Element,
        view: Element,
        viewType: Int
    ): Boolean {
        val inflaterClass = ClassName("android.view", "LayoutInflater")
        return generateClass(
            type = type,
            configurator = mediator,
            view = view,
            viewType = viewType,
            superClassInit = {
                addSuperclassConstructorParameter("view")
                addSuperclassConstructorParameter("view.root")
            },
            factoryInit = {
                val viewClass = view.getClassName()
                addStatement("val inflater = %T.from(parent.context)", inflaterClass)
                addStatement("val view = %T.inflate(inflater, parent, false)", viewClass)
            }
        )
    }

    private fun generateClass(
        type: ViewHolderType,
        configurator: Element,
        view: Element,
        viewType: Int,
        superClassInit: TypeSpec.Builder.() -> Unit,
        factoryInit: FunSpec.Builder.() -> Unit
    ): Boolean {
        return when (type) {
            ViewHolderType.ALL ->
                generateClass(ViewHolderType.RECYCLER_VIEW, configurator, view, viewType, superClassInit, factoryInit)
                    && generateClass(ViewHolderType.VIEW_GROUP, configurator, view, viewType, superClassInit, factoryInit)
            else -> generateClass(type.packageSuffix, configurator, view, viewType, superClassInit, factoryInit)
        }
    }

    private fun generateClass(
        packageSuffix: String,
        configurator: Element,
        view: Element,
        viewType: Int,
        superClassInit: TypeSpec.Builder.() -> Unit,
        factoryInit: FunSpec.Builder.() -> Unit
    ): Boolean {
        val elementUtils = processingEnv.elementUtils
        val packageName = elementUtils.getPackageOf(configurator).toString()
            .plus(packageSuffix)
        val className = configurator.simpleName.toString().replace("Configurator", "")
            .plus("ViewHolder")
        val fileBuilder = FileSpec.builder(packageName, className)

        val viewHolderClass = ClassName(packageName, className)
        val viewClass = view.getClassName()
        val configuratorClass = configurator.getClassName()
        val baseConfiguratorHolderClass = ClassName(
            "org.sdelaysam.configurator$packageSuffix",
            "ConfiguratorViewHolder"
        ).parameterizedBy(viewClass, configuratorClass)
        val baseViewHolderClass = ClassName(
            "org.sdelaysam.configurator$packageSuffix",
            "BasicViewHolder"
        )
        val baseViewHolderFactoryClass = ClassName(
            "org.sdelaysam.configurator$packageSuffix",
            "BasicViewHolder",
            "Factory"
        )
        val viewGroupClass = ClassName("android.view", "ViewGroup")
        val classBuilder = TypeSpec.classBuilder(className)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("view", viewClass)
                    .build()
            )
            .superclass(baseConfiguratorHolderClass)
            .also(superClassInit)
            .addType(
                TypeSpec.classBuilder("Factory")
                    .addSuperinterface(baseViewHolderFactoryClass)
                    .addProperty(
                        PropertySpec
                            .builder("viewType", Int::class)
                            .addModifiers(KModifier.OVERRIDE)
                            .initializer("%L", viewType)
                            .build()
                    )
                    .addFunction(
                        FunSpec
                            .builder("create")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(baseViewHolderClass)
                            .addParameter(
                                ParameterSpec
                                    .builder("parent", viewGroupClass)
                                    .build()
                            )
                            .also(factoryInit)
                            .addStatement("return %T(view)", viewHolderClass)
                            .build()
                    )
                    .build()
            )

        val file = fileBuilder.addType(classBuilder.build()).build()
        val outDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!
        file.writeTo(File(outDir))
        return true
    }

    private fun Element.getClassName(): ClassName {
        return ClassName(
            processingEnv.elementUtils.getPackageOf(this).toString(),
            simpleName.toString()
        )
    }

    private val ViewHolderType.packageSuffix: String
        get() = when (this) {
            ViewHolderType.RECYCLER_VIEW -> ".recyclerview"
            ViewHolderType.VIEW_GROUP -> ".viewgroup"
            else -> ""
        }

}