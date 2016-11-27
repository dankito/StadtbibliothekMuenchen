package net.dankito.stadtbibliothekmuenchen;

import android.app.Application;

import net.dankito.stadtbibliothekmuenchen.di.AndroidDiComponent;
import net.dankito.stadtbibliothekmuenchen.di.AndroidDiContainer;
import net.dankito.stadtbibliothekmuenchen.di.DaggerAndroidDiComponent;

/**
 * Created by ganymed on 27/11/16.
 */

public class StadtbibliothekMuenchenApplication extends Application {

  protected AndroidDiComponent component;

  @Override
  public void onCreate() {
    super.onCreate();

    setupDependencyInjection();
  }

  protected void setupDependencyInjection() {
    component = DaggerAndroidDiComponent.builder()
        .androidDiContainer(new AndroidDiContainer(this))
        .build();
  }


  public AndroidDiComponent getComponent() {
    return component;
  }
}
