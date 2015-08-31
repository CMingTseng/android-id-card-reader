package com.eftimoff.idcardreader.ui.choose;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.eftimoff.idcardreader.R;
import com.eftimoff.idcardreader.components.passport.DaggerPassportComponent;
import com.eftimoff.idcardreader.components.passport.PassportService;
import com.eftimoff.idcardreader.models.Passport;
import com.eftimoff.idcardreader.ui.common.BaseFragment;

import java.io.Serializable;
import java.util.List;

import butterknife.Bind;
import butterknife.BindInt;
import rx.Observable;
import rx.functions.Action1;

public class ChooseFragment extends BaseFragment {

    public interface ChooseFragmentDelegate extends Serializable {
        void onChoose(final Passport passport);
    }

    ///////////////////////////////////
    ///          CONSTANTS          ///
    ///////////////////////////////////

    ///////////////////////////////////
    ///            VIEWS            ///
    ///////////////////////////////////

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    ///////////////////////////////////
    ///            FIELDS           ///
    ///////////////////////////////////

    private PassportService passportService;
    private PassportAdapter passportAdapter;
    private ChooseFragmentDelegate delegate;

    ///////////////////////////////////
    ///          RESOURCES          ///
    ///////////////////////////////////

    @BindInt(R.integer.fragment_choose_column_number)
    int columnCount;


    public static ChooseFragment getInstance() {
        return new ChooseFragment();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            delegate = (ChooseFragmentDelegate) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ChooseFragmentDelegate");
        }
    }

    @Override
    protected int layoutResourceId() {
        return R.layout.fragment_choose;
    }

    @Override
    protected void setupComponents() {
        passportService = DaggerPassportComponent.create().provideCountryService();
    }

    @Override
    protected void setupViews() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnCount));
        passportAdapter = new PassportAdapter();
        passportAdapter.setListener(passportChosenListener);
        recyclerView.setAdapter(passportAdapter);
    }

    @Override
    protected void init() {
        final Observable<List<Passport>> observableCountries = passportService.getCountries();
        observableCountries.subscribe(new Action1<List<Passport>>() {
            @Override
            public void call(final List<Passport> countries) {
                passportAdapter.setPassportList(countries);
            }
        });
    }

    private final PassportChosenListener passportChosenListener = new PassportChosenListener() {
        @Override
        public void onChoose(final Passport passport) {
            delegate.onChoose(passport);
        }
    };
}
