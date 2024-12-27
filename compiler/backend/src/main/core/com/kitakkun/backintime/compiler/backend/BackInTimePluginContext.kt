package com.kitakkun.backintime.compiler.backend

import com.kitakkun.backintime.compiler.backend.analyzer.UserDefinedValueContainerAnalyzer
import com.kitakkun.backintime.compiler.backend.api.VersionSpecificAPI
import com.kitakkun.backintime.compiler.backend.valuecontainer.ValueContainerBuiltIns
import com.kitakkun.backintime.compiler.backend.valuecontainer.resolved.ResolvedValueContainer
import com.kitakkun.backintime.compiler.common.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class BackInTimePluginContext(
    baseContext: IrPluginContext,
    moduleFragment: IrModuleFragment,
) : IrPluginContext by baseContext {
    val pluginContext: IrPluginContext = baseContext
    val valueContainerClassInfoList: List<ResolvedValueContainer> = ValueContainerBuiltIns.mapNotNull {
        val resolvedValueContainer = ResolvedValueContainer.create(it)
        if (resolvedValueContainer == null) {
            MessageCollectorHolder.reportWarning("Could not resolve value container: ${it.classId}")
        }
        resolvedValueContainer
    } + UserDefinedValueContainerAnalyzer.analyzeAdditionalValueContainerClassInfo(moduleFragment)

    // event report functions
    val reportInstanceRegistrationFunctionSymbol by lazy { backintimeNamedFunction(name = "reportInstanceRegistration", subpackage = "core.runtime.internal") }
    val reportMethodInvocationFunctionSymbol by lazy { backintimeNamedFunction(name = "reportMethodInvocation", subpackage = "core.runtime.internal") }
    val reportPropertyValueChangeFunctionSymbol by lazy { backintimeNamedFunction(name = "reportPropertyValueChange", subpackage = "core.runtime.internal") }
    val reportNewRelationshipFunctionSymbol by lazy { backintimeNamedFunction(name = "reportNewRelationship", subpackage = "core.runtime.internal") }

    // error generation functions
    val throwTypeMismatchExceptionFunctionSymbol by lazy { backintimeNamedFunction(name = "throwTypeMismatchException", subpackage = "core.runtime.internal") }
    val throwNoSuchPropertyExceptionFunctionSymbol by lazy { backintimeNamedFunction(name = "throwNoSuchPropertyException", subpackage = "core.runtime.internal") }

    // capture utils
    val captureThenReturnValueFunctionSymbol by lazy { backintimeNamedFunction(name = "captureThenReturnValue", subpackage = "core.runtime.internal") }

    /**
     * Used in [com.kitakkun.backintime.compiler.backend.transformer.BackInTimeDebuggableConstructorTransformer]
     */
    val propertyInfoClass by lazy { backintimeIrClassSymbol(name = "PropertyInfo", subpackage = "core.websocket.event.model") }
    val propertyInfoClassConstructor = propertyInfoClass.constructors.first { it.owner.isPrimary }
    val listOfFunction by lazy { namedFunction("kotlin.collections", "listOf") { it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().isVararg } }

    // kotlinx-serialization
    val backInTimeJsonGetter = referenceProperties(BackInTimeConsts.backInTimeJsonCallableId).single().owner.getter!!
    val encodeToStringFunction by lazy {
        namedFunction("kotlinx.serialization", "encodeToString") {
            VersionSpecificAPI.INSTANCE.isReifiable(it.owner) && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
        }
    }
    val decodeFromStringFunction by lazy {
        namedFunction("kotlinx.serialization", "decodeFromString") {
            VersionSpecificAPI.INSTANCE.isReifiable(it.owner) && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
        }
    }

    // uuid
    val uuidFunctionSymbol by lazy { backintimeNamedFunction(name = "uuid", subpackage = "core.runtime.internal") }

    val mutableMapOfFunction by lazy { namedFunction("kotlin.collections", "mutableMapOf") }

    private fun namedFunction(
        packageName: String,
        name: String,
        filter: (IrSimpleFunctionSymbol) -> Boolean = { true },
    ): IrSimpleFunctionSymbol {
        val callableId = CallableId(FqName(packageName), Name.identifier(name))
        return pluginContext.referenceFunctions(callableId).first(filter)
    }

    private fun backintimeNamedFunction(name: String, subpackage: String? = null): IrSimpleFunctionSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return namedFunction("com.kitakkun.backintime$suffix", name)
    }

    private fun backintimeIrClassSymbol(name: String, subpackage: String? = null): IrClassSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return getIrClassSymbol("com.kitakkun.backintime$suffix", name)
    }

    private fun getIrClassSymbol(packageName: String, name: String): IrClassSymbol = pluginContext.referenceClass(classId(packageName, name))
        ?: error("Unable to find symbol. Package: $packageName, Name: $name")
}
