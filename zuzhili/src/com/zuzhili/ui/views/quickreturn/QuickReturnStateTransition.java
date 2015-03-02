package com.zuzhili.ui.views.quickreturn;

public interface QuickReturnStateTransition {
    public int determineState(final int rawY, int quickReturnHeight);
}
