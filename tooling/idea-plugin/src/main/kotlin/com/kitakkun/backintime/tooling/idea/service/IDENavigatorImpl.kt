package com.kitakkun.backintime.tooling.idea.service

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.util.ClassUtil
import com.intellij.psi.util.PropertyUtil
import com.kitakkun.backintime.tooling.core.shared.IDENavigator

@Service(Service.Level.PROJECT)
class IDENavigatorImpl(project: Project) : IDENavigator {
    private val psiManager = PsiManager.getInstance(project)

    override fun navigateToClass(classSignature: String) {
        val jvmBasedClassSignature = classSignature.replace(".", "$").replace("/", ".")

        ApplicationManager.getApplication().executeOnPooledThread {
            val psiClass = ReadAction.compute<PsiClass?, Throwable> {
                ClassUtil.findPsiClass(psiManager, jvmBasedClassSignature)
            }
            if (psiClass == null) return@executeOnPooledThread
            ApplicationManager.getApplication().invokeLater {
                ReadAction.run<Throwable> {
                    thisLogger().debug("Navigating to ${psiClass.nameIdentifier}")
                    psiClass.navigate(true)
                }
            }
        }
    }

    override fun navigateToMemberProperty(propertySignature: String) {
        val propertyName = propertySignature.split(".").last()
        val classSignature = propertySignature.removeSuffix(".$propertyName")
        val jvmBasedClassSignature = classSignature.replace(".", "$").replace("/", ".")

        ApplicationManager.getApplication().executeOnPooledThread {
            val psiClass = ReadAction.compute<PsiClass?, Throwable> {
                ClassUtil.findPsiClass(psiManager, jvmBasedClassSignature)
            } ?: return@executeOnPooledThread
            val psiProperty = PropertyUtil.findPropertyField(psiClass, propertyName, false) ?: return@executeOnPooledThread
            ApplicationManager.getApplication().invokeLater {
                ReadAction.run<Throwable> {
                    thisLogger().debug("Navigating to ${psiProperty.nameIdentifier}")
                    psiProperty.navigate(true)
                }
            }
        }
    }

    override fun navigateToMemberFunction(functionSignature: String) {
        TODO("Not implemented yet.")
    }
}
