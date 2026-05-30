import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ToastContainerComponent } from './core/components/toast-container/toast-container.component';
import { httpErrorInterceptor } from './core/interceptors/http-error.interceptor';

@NgModule({
  declarations: [AppComponent, ToastContainerComponent],
  imports: [BrowserModule, AppRoutingModule, FormsModule, ReactiveFormsModule],
  providers: [
    provideHttpClient(withInterceptors([httpErrorInterceptor])),
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
