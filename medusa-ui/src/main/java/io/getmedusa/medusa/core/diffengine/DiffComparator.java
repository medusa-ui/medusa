package io.getmedusa.medusa.core.diffengine;

import io.getmedusa.medusa.core.router.action.JSReadyDiff;

import java.util.Comparator;

public class DiffComparator implements Comparator<JSReadyDiff> {

    //if o1 < o2, then should return negative
    //if I want something last, I would want it as the highest number
    @Override
    public int compare(JSReadyDiff o1, JSReadyDiff o2) {
        //so imagine o1 is selected=true and o2 does not have selected
        //that means o1>o2, because I want o1 to be last, so have the highest number
        //that means I should return a positive number

        int o1Value = 0;
        int o2Value = 0;

        if(o1.isEdit() && o1.getContent().contains("selected=\"selected\"")) {
            o1Value = 1;
        }

        if(o2.isEdit() && o2.getContent().contains("selected=\"selected\"")) {
            o2Value = 1;
        }

        return o1Value - o2Value;
    }

}
