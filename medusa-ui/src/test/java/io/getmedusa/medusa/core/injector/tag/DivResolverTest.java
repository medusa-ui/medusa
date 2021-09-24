package io.getmedusa.medusa.core.injector.tag;

import io.getmedusa.medusa.core.injector.tag.meta.Div;
import io.getmedusa.medusa.core.injector.tag.meta.DivResolver;
import io.getmedusa.medusa.core.injector.tag.meta.ForEachElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class DivResolverTest {

    public static final ForEachElement SIMPLE_ELEM = elem("<div class='welcome'>Hello world</div>");
    public static final ForEachElement LOOP_ELEM = elem("[$foreach $persons]<div class='welcome'>[$each]</div>[$end for]");
    public static final ForEachElement LOOP_MULTI_ELEM = elem("[$foreach $persons]<div>[$each]</div> [$each] <span>[$each]</span>[$end for]");

    public static final ForEachElement NESTED_ELEM_PARENT = elem("[$foreach $persons][$foreach $apples]<div class='welcome'>[$each]</div>[$end for][$end for]");
    public static final ForEachElement NESTED_ELEM_CHILD = elem("[$foreach $apples]<div class='welcome'>[$each]</div>[$end for]");

    public static final ForEachElement NESTED_OTHR_ELEM_PARENT = elem("[$foreach $persons]Person [$each], with apples: [$foreach $apples]<div class='apple'>[$each]</div>[$end for][$end for]");
    public static final ForEachElement NESTED_OTHR_ELEM_CHILD = elem("Person [$each], with apples: [$foreach $apples]<div class='apple'>[$each]</div>[$end for]");

    static ForEachElement elem(String outer) {
        return new ForEachElement(outer, innerBlock(outer));
    }

    static String innerBlock(String outer) {
        if(!outer.contains("s]")) return outer;
        return outer.substring(outer.indexOf("s]") + 2, outer.lastIndexOf("[$end"));
    }

    @Test
    void testBasic() {
        Div div = new Div(SIMPLE_ELEM, "Medusa", null);
        String resolvedDiv = DivResolver.resolve(div);
        Assertions.assertEquals("<div class='welcome'>Hello world</div>", resolvedDiv);
    }

    @Test
    void testSingleLoop() {
        Div div = new Div(LOOP_ELEM, "Medusa", null);
        String resolvedDiv = DivResolver.resolve(div);
        Assertions.assertEquals("<div class='welcome'>Medusa</div>", resolvedDiv);
    }

    @Test
    void testSingleLoopMultiOccurrence() {
        Div div = new Div(LOOP_MULTI_ELEM, "Medusa", null);
        String resolvedDiv = DivResolver.resolve(div);
        Assertions.assertEquals("<div>Medusa</div> Medusa <span>Medusa</span>", resolvedDiv);
    }

    @Test
    void testSingleNestedLoopNoDataOtherThanLoops() {
        Div divParent = new Div(NESTED_ELEM_PARENT, null, null);
        Div div = new Div(NESTED_ELEM_CHILD, "Child", divParent);
        String resolvedDiv = DivResolver.resolve(div);
        Assertions.assertEquals("<div class='welcome'>Child</div>", resolvedDiv);
    }

    @Test
    void testMultiEachObjNestedLoopNoDataOtherThanLoops() {
        Div divParent = new Div(NESTED_ELEM_PARENT, null, null);
        Div div = new Div(NESTED_ELEM_CHILD, Arrays.asList("Child 1", "Child 2"), divParent);
        String resolvedDiv = DivResolver.resolve(div);
        Assertions.assertEquals("<div class='welcome'>[Child 1, Child 2]</div>", resolvedDiv);
    }

    @Test
    void testNestedLoopWithOtherData() {
        Div divParent = new Div(NESTED_OTHR_ELEM_PARENT, "Parent", null);
        Div div = new Div(NESTED_OTHR_ELEM_CHILD, "Child", divParent);
        div.setResolvedHTML(DivResolver.resolve(div));
        String resolvedDiv = DivResolver.resolve(divParent);
        Assertions.assertEquals("Person Parent, with apples: <div class='apple'>Child</div>", resolvedDiv);
    }

    @Test
    void testTemplateSimple() {

    }

    @Test
    void testTemplateNested() {
        Div divParent = new Div(NESTED_OTHR_ELEM_PARENT, "Parent", null);
        Div div = new Div(NESTED_OTHR_ELEM_CHILD, "Child", divParent);

        divParent.getChildren().add(div);

        String template = DivResolver.buildTemplate(divParent.getElement());

        System.out.println(template);
    }

}
