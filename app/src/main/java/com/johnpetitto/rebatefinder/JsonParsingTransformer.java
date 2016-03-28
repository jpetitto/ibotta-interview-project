package com.johnpetitto.rebatefinder;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class JsonParsingTransformer<R> implements Single.Transformer<InputStream, R> {
  private Type responseType;

  public JsonParsingTransformer(Type responseType) {
    this.responseType = responseType;
  }

  @Override public Single<R> call(Single<InputStream> inputStreamSingle) {
    return inputStreamSingle.subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(Schedulers.io())
        .map(new Func1<InputStream, JsonReader>() {
          @Override public JsonReader call(InputStream stream) {
            return new JsonReader(new InputStreamReader(stream));
          }
        })
        .map(new Func1<JsonReader, R>() {
          @Override public R call(JsonReader jsonReader) {
            R response = new Gson().fromJson(jsonReader, responseType);
            Utils.closeResourceQuietly(jsonReader);
            return response;
          }
        });
  }
}
