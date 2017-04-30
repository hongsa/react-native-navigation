package com.reactnativenavigation.viewcontrollers;

import android.app.Activity;
import android.view.View;

import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.SimpleViewController;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ViewControllerTest extends BaseTest {

	private ViewController uut;
	private Activity activity;

	@Override
	public void beforeEach() {
		super.beforeEach();
		activity = newActivity();
		uut = new SimpleViewController(activity, "uut");
	}

	@Test
	public void holdsAView() throws Exception {
		assertThat(uut.getView()).isNotNull().isInstanceOf(View.class);
	}

	@Test
	public void holdsARefToActivity() throws Exception {
		assertThat(uut.getActivity()).isNotNull().isEqualTo(activity);
	}

	@Test
	public void canOverrideViewCreation() throws Exception {
		final View otherView = new View(activity);
		ViewController myController = new ViewController(activity) {
			@Override
			protected View createView() {
				return otherView;
			}
		};
		assertThat(myController.getView()).isEqualTo(otherView);
	}

	@Test
	public void holdsAReferenceToStackControllerOrNull() throws Exception {
		assertThat(uut.getStackController()).isNull();
		StackController nav = new StackController(activity);
		nav.push(uut);
		assertThat(uut.getStackController()).isEqualTo(nav);
	}

	@Test
	public void handleBackDefaultFalse() throws Exception {
		assertThat(uut.handleBack()).isFalse();
	}

	public static class LifecycleTest extends BaseTest {
		private ViewController controller;
		private ViewController.LifecycleListener uut;

		@Override
		public void beforeEach() {
			super.beforeEach();
			Activity activity = newActivity();
			controller = new SimpleViewController(activity, "controller");
			uut = mock(ViewController.LifecycleListener.class);
			controller.setLifecycleListener(uut);
		}

		@Test
		public void onCreateView_CalledAsSoonAsPossible() throws Exception {
			verifyZeroInteractions(uut);
			controller.getView();
			verify(uut, times(1)).onCreate();
		}

		@Test
		public void onStart_CalledWhenVisible() throws Exception {
			verifyZeroInteractions(uut);
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			verify(uut, times(1)).onStart();
		}

		@Test
		public void onStop_CalledWhenInvisible() throws Exception {
			verifyZeroInteractions(uut);
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			controller.getView().setVisibility(View.GONE);
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			verify(uut, times(1)).onStop();
		}

		@Test
		public void onStart_OnStop_Cycle() throws Exception {
			verifyZeroInteractions(uut);
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			verify(uut, times(1)).onStart();
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			controller.getView().setVisibility(View.INVISIBLE);
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			verify(uut, times(1)).onStop();
			controller.getView().setVisibility(View.VISIBLE);
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			controller.getView().getViewTreeObserver().dispatchOnGlobalLayout();
			verify(uut, times(1)).onStart();
		}
	}

}