package com.johnpetitto.rebatefinder;

import android.content.res.AssetManager;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public final class Utils {
  private Utils() {}

  public static InputStream openStreamQuietly(AssetManager manager, String filename) {
    try {
      return manager.open(filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void closeResourceQuietly(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
