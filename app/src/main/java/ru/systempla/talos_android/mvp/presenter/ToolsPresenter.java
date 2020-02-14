package ru.systempla.talos_android.mvp.presenter;

import io.reactivex.Scheduler;
import moxy.InjectViewState;
import moxy.MvpPresenter;
import ru.systempla.talos_android.mvp.view.ToolsView;

@InjectViewState
public class ToolsPresenter extends MvpPresenter<ToolsView> {

    private Scheduler mainThreadScheduler;
    private Scheduler ioThreadScheduler;

    public ToolsPresenter(Scheduler mainThreadScheduler, Scheduler ioThreadScheduler) {
        this.mainThreadScheduler = mainThreadScheduler;
        this.ioThreadScheduler = ioThreadScheduler;
    }
}