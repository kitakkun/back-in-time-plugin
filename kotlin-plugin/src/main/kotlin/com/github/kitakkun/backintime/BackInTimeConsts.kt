package com.github.kitakkun.backintime

import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeConsts {
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.backintime.runtime.DebuggableStateHolderManipulator")

    val forceSetPropertyValueForBackInDebugMethodName = Name.identifier("forceSetPropertyValueForBackInTimeDebug")
    val firstParameterNameForGeneratedMethod = Name.identifier("propertyName")
    val secondParameterNameForGeneratedMethod = Name.identifier("value")

    val mutableLiveDataClassId = classId("androidx.lifecycle", "MutableLiveData")
    val mutableLiveDataFqName = mutableLiveDataClassId.asSingleFqName()
    val mutableStateFlowFqName = FqName("kotlinx.coroutines.flow.MutableStateFlow")
    val mutableStateFqName = FqName("androidx.compose.runtime.MutableState")

    val mutableLiveDataPostValueCallableId = CallableId(mutableLiveDataClassId, Name.identifier("postValue"))

    // StackTrace経由での呼び出し元メソッド名解決を行うための定数
    val getStackTraceFqName = FqName("kotlin.Throwable.getStackTrace")
    val stackTraceGetMethodNameCallableId = CallableId(FqName("java.lang"), FqName("StackTraceElement"), Name.identifier("getMethodName"))
    val printlnCallableId = CallableId(FqName("kotlin.io"), Name.identifier("println"))
}
