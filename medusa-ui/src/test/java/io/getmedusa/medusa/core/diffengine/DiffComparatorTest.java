package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class DiffComparatorTest {

    private static final DiffComparator DIFF_COMPARATOR = new DiffComparator();

    @Test
    void testSelectedValuesComeLast() {
        JSReadyDiff optionNotSelected1 = JSReadyDiff.buildNewEdit("/html[1]/body[1]/select[1]/option[6]", "<option>Strawberry</option>");
        JSReadyDiff optionSelected = JSReadyDiff.buildNewEdit("/html[1]/body[1]/select[1]/option[7]", "<option selected=\"selected\">Banana</option>");
        JSReadyDiff optionNotSelected2 = JSReadyDiff.buildNewEdit("/html[1]/body[1]/select[1]/option[2]", "<option>Orange</option>");

        List<JSReadyDiff> diffs = new ArrayList<>(List.of(optionNotSelected1, optionSelected, optionNotSelected2));
        diffs.sort(DIFF_COMPARATOR);
        Assertions.assertEquals(optionSelected.getContent(), diffs.get(2).getContent());
    }

}
