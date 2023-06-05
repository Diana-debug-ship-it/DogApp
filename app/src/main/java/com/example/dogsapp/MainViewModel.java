package com.example.dogsapp;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Discouraged;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private static final String BASE_URL = "https://dog.ceo/api/breeds/image/";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATUS = "status";
    private static final String TAG = "MainViewModel";

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<DogImage> dogImage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<DogImage> getDogImage() {
        return dogImage;
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public void loadDogImage() {
        Disposable disposable = loadDogImageRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        isError.setValue(false);
                        isLoading.setValue(true);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        isError.setValue(true);
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Throwable {
                        isLoading.setValue(false);
                    }
                })
                .subscribe(new Consumer<DogImage>() {
                    @Override
                    public void accept(DogImage image) throws Throwable {
                        dogImage.setValue(image);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Single<DogImage> loadDogImageRx() {
        return ApiFactory.getApiService().loadDogImage();
//        return Single.fromCallable(new Callable<DogImage>() {
//            @Override
//            public DogImage call() throws Exception {
//                URL url = new URL(BASE_URL);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                InputStream inputStream = urlConnection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String result;
//                StringBuilder data = new StringBuilder();
//                do {
//                    result = bufferedReader.readLine();
//                    data.append(result);
//                } while (result != null);
//                JSONObject jsonObject = new JSONObject(data.toString());
//                String message = jsonObject.getString(KEY_MESSAGE);
//                String status = jsonObject.getString(KEY_STATUS);
//                return new DogImage(message, status);
//            }
//        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
