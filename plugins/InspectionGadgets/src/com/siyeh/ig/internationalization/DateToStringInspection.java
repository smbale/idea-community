/*
 * Copyright 2003-2007 Dave Griffith, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.internationalization;

import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiType;
import com.siyeh.HardcodedMethodConstants;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.psiutils.MethodCallUtils;
import com.siyeh.ig.psiutils.TypeUtils;
import org.jetbrains.annotations.NotNull;

public class DateToStringInspection extends BaseInspection {

    @NotNull
    public String getID() {
        return "CallToDateToString";
    }

    @NotNull
    public String getDisplayName() {
        return InspectionGadgetsBundle.message(
                "call.to.date.tostring.display.name");
    }

    @NotNull
    public String buildErrorString(Object... infos) {
        return InspectionGadgetsBundle.message(
                "call.to.date.tostring.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new DateToStringVisitor();
    }

    private static class DateToStringVisitor extends BaseInspectionVisitor {

        @Override public void visitMethodCallExpression(
                @NotNull PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final String methodName = MethodCallUtils.getMethodName(expression);
            if (!HardcodedMethodConstants.TO_STRING.equals(methodName)) {
                return;
            }
            final PsiType targetType = MethodCallUtils.getTargetType(expression);
            if (!TypeUtils.typeEquals("java.util.Date", targetType)) {
                return;
            }
            final PsiExpressionList argumentList = expression.getArgumentList();
            if (argumentList.getExpressions().length != 0) {
                return;
            }
            if (NonNlsUtils.isNonNlsAnnotatedUse(expression)) {
                return;
            }
            registerMethodCallError(expression);
        }
    }
}