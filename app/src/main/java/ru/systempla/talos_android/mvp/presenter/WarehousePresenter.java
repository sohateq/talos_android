package ru.systempla.talos_android.mvp.presenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import moxy.InjectViewState;
import moxy.MvpPresenter;
import ru.systempla.talos_android.mvp.model.entity.Product;
import ru.systempla.talos_android.mvp.model.repo.ITalosRepo;
import ru.systempla.talos_android.mvp.view.WarehouseView;
import ru.systempla.talos_android.mvp.view.list.WarehouseItemView;

@InjectViewState
public class WarehousePresenter extends MvpPresenter<WarehouseView> {

    @Inject
    ITalosRepo talosRepo;

    private Scheduler mainThreadScheduler;
    private Scheduler ioThreadScheduler;
    private WarehouseListPresenter warehouseListPresenter;

    class WarehouseListPresenter implements IWarehouseListPresenter {

        PublishSubject<WarehouseItemView> clickSubject = PublishSubject.create();
        List<Product> warehouseBlocks = new ArrayList<>();

        @Override
        public void bind(WarehouseItemView view) {
            view.setName(warehouseBlocks.get(view.getPos()).getName());
            view.setStatus(warehouseBlocks.get(view.getPos()).getStatus());
            view.setCount(warehouseBlocks.get(view.getPos()).getCount());
        }

        @Override
        public int getCount() {
            return warehouseBlocks.size();
        }

        @Override
        public PublishSubject<WarehouseItemView> getClickSubject() {
            return clickSubject;
        }
    }

    public WarehousePresenter(Scheduler mainThreadScheduler, Scheduler ioThreadScheduler) {
        this.mainThreadScheduler = mainThreadScheduler;
        this.ioThreadScheduler = ioThreadScheduler;
        warehouseListPresenter = new WarehouseListPresenter();
    }

    public IWarehouseListPresenter getWarehouseListPresenter() {
        return warehouseListPresenter;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().init();
    }

    public void loadWarehouseData(){
        getViewState().showLoading();
        Disposable disposable = talosRepo.loadProducts()
                .subscribeOn(ioThreadScheduler)
                .observeOn(mainThreadScheduler)
                .subscribe(model -> {
                    warehouseListPresenter.warehouseBlocks.clear();
                    warehouseListPresenter.warehouseBlocks.addAll(model);
                    getViewState().updateList();
                    getViewState().hideLoading();
                }, t -> {
                    getViewState().showMessage("Ошибка загрузки данных");
                    getViewState().hideLoading();
                });
    }
}
