package io.getmedusa.medusa.core.injector.tag;

import org.junit.jupiter.api.Test;

class TableTagTest {

    //head
    //footer
    //classes
    //colspan
    //pagination

    private static final String TABLE_SAMPLE_SIMPLE = """
            <m:table class="custom-table">
              <m:head class="custom-head">
                <m:th class="custom-th-1">Company</m:th>
                <m:th class="custom-th-2">Country</m:th>
              </m:head>
              <m:tr class="custom-tr-static-1">
                <m:td class="custom-td-static-1">Alfreds Futterkiste</td>
                <m:td class="custom-td-static-2">Germany</td>
              </tr>
              <m:tr collection="coworkers" eachName="coworker" class="custom-tr-dynamic">
                <m:td class="custom-td-dynamic-1"><m:text item="coworker.company"/></td>
                <m:td class="custom-td-dynamic-2"><span><m:text item="coworker.name"/></td>
              </m:tr>
              <m:tr class="custom-tr-static-2">
                <m:td class="custom-td-static-3">Magazzini Alimentari Riuniti</m:td>
                <m:td class="custom-td-static-4">Italy</m:td>
              </m:tr>
            </m:table>
            """;

    @Test
    void testTableToDivTranslation() {

    }

}
