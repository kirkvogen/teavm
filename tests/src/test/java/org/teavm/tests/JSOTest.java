/*
 *  Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;
import org.teavm.diagnostics.Problem;
import org.teavm.jso.JSBody;
import org.teavm.model.MethodReference;
import org.teavm.model.ValueType;
import org.teavm.vm.TeaVM;
import org.teavm.vm.TeaVMBuilder;

/**
 *
 * @author Alexey Andreev
 */
public class JSOTest {
    @Test
    public void reportsAboutWrongParameterOfJSBody() {
        Problem foundProblem = build("callJSBodyWithWrongParameter").stream().filter(problem -> {
            return problem.getLocation().getMethod().getName().equals("callJSBodyWithWrongParameter")
                    && problem.getText().equals("Method {{m0}} is not a proper native JavaScript method "
                        + " declaration. Its parameter #1 has invalid type {{t1}}");
        }).findAny().orElse(null);

        assertNotNull(foundProblem);
        Object[] params = foundProblem.getParams();
        assertThat(params[0], is(new MethodReference(JSOTest.class, "jsBodyWithWrongParameter",
                Object.class, void.class)));
        assertThat(params[1], is(ValueType.parse(Object.class)));
    }

    private static void callJSBodyWithWrongParameter() {
        jsBodyWithWrongParameter(23);
    }

    @JSBody(params = "param", script = "alert(param.toString());")
    private static native void jsBodyWithWrongParameter(Object param);

    @Test
    public void reportsAboutWrongNonStaticJSBody() {
        Problem foundProblem = build("callWrongNonStaticJSBody").stream().filter(problem -> {
            return problem.getLocation().getMethod().getName().equals("callWrongNonStaticJSBody")
                    && problem.getText().equals("Method {{m0}} is not a proper native JavaScript method "
                        + " declaration. It is non-static and declared on a non-overlay class {{c1}}");
        }).findAny().orElse(null);

        assertNotNull(foundProblem);
        Object[] params = foundProblem.getParams();
        assertThat(params[0], is(new MethodReference(JSOTest.class, "wrongNonStaticJSBody", void.class)));
        assertThat(params[1], is(JSOTest.class.getName()));
    }

    private static void callWrongNonStaticJSBody() {
        new JSOTest().wrongNonStaticJSBody();
    }

    @JSBody(params = {}, script = "alert(this.toString());")
    private native void wrongNonStaticJSBody();

    @Test
    public void reportsAboutJSBodyWithWrongReturningType() {
        Problem foundProblem = build("callJSBodyWithWrongReturningType").stream().filter(problem -> {
            return problem.getLocation().getMethod().getName().equals("callJSBodyWithWrongReturningType")
                    && problem.getText().equals("Method {{m0}} is not a proper native JavaScript method "
                            + " declaration, since it returns invalid type {{t1}}");
        }).findAny().orElse(null);

        assertNotNull(foundProblem);
        Object[] params = foundProblem.getParams();
        assertThat(params[0], is(new MethodReference(JSOTest.class, "jsBodyWithWrongReturningType", String.class,
                Object.class)));
        assertThat(params[1], is(ValueType.parse(Object.class)));
    }

    private static void callJSBodyWithWrongReturningType() {
        jsBodyWithWrongReturningType("foo");
    }

    @JSBody(params = "value", script = "return value;")
    private static native Object jsBodyWithWrongReturningType(String value);

    private List<Problem> build(String methodName) {
        TeaVM vm = new TeaVMBuilder().build();
        vm.installPlugins();
        vm.entryPoint("test", new MethodReference(JSOTest.class, methodName, void.class));
        vm.build(new StringBuilder(), null);
        return vm.getProblemProvider().getSevereProblems();
    }
}
