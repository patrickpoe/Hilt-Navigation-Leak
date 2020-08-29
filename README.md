# Hilt-Navigation-Leak
Project to reproduce a memory leak when using Hilt and Navigation Component reported [Here](https://github.com/google/dagger/issues/2070)

## Set Up

The project has 4 fragments hooked up thorugh 	[Navigaiton Component](https://developer.android.com/guide/navigation)

* MainFragment is the start navigation and lets you navigate to the other fragments.

* LeakFragment is marked with `@AndroidEnryPoint` so it can be injected with Hilt. It also updates the toolbar through the navigation component as suggested in the [official docs](https://developer.android.com/guide/navigation/navigation-ui).
As the name suggest, this fragment introduce the memory leak.

* NavigationOnlyFragment updates the toolbar thorugh the navigation compoenent, but does not mark the fragment as an entry point for hilt, hence there is no memory leak.

* HiltOnlyFragment is marked as an android entry point, but since it doesn´t updates the toolbar there is no leak either.


## How to reproduce

Simply navigate to the `LeakFragment`and back a few times. Then wait until leak canary reports the leak.

## Leak

```
┬───
│ GC Root: System class
│
├─ android.view.inputmethod.InputMethodManager class
│    Leaking: NO (InputMethodManager↓ is not leaking and a class is never leaking)
│    ↓ static InputMethodManager.sInstance
├─ android.view.inputmethod.InputMethodManager instance
│    Leaking: NO (DecorView↓ is not leaking and InputMethodManager is a singleton)
│    ↓ InputMethodManager.mNextServedView
├─ com.android.internal.policy.DecorView instance
│    Leaking: NO (LinearLayout↓ is not leaking and View attached)
│    mContext instance of com.android.internal.policy.DecorContext, wrapping activity com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    Parent android.view.ViewRootImpl not a android.view.View
│    View#mParent is set
│    View#mAttachInfo is not null (view attached)
│    View.mWindowAttachCount = 1
│    ↓ DecorView.mContentRoot
├─ android.widget.LinearLayout instance
│    Leaking: NO (MainActivity↓ is not leaking and View attached)
│    mContext instance of com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    View.parent com.android.internal.policy.DecorView attached as well
│    View#mParent is set
│    View#mAttachInfo is not null (view attached)
│    View.mWindowAttachCount = 1
│    ↓ LinearLayout.mContext
├─ com.mpierucci.android.hiltmemleak.MainActivity instance
│    Leaking: NO (NavHostFragment↓ is not leaking and Activity#mDestroyed is false)
│    ↓ MainActivity.mFragments
├─ androidx.fragment.app.FragmentController instance
│    Leaking: NO (NavHostFragment↓ is not leaking)
│    ↓ FragmentController.mHost
├─ androidx.fragment.app.FragmentActivity$HostCallbacks instance
│    Leaking: NO (NavHostFragment↓ is not leaking)
│    ↓ FragmentActivity$HostCallbacks.mFragmentManager
├─ androidx.fragment.app.FragmentManagerImpl instance
│    Leaking: NO (NavHostFragment↓ is not leaking)
│    ↓ FragmentManagerImpl.mPrimaryNav
├─ androidx.navigation.fragment.NavHostFragment instance
│    Leaking: NO (Fragment#mFragmentManager is not null)
│    ↓ NavHostFragment.mNavController
│                      ~~~~~~~~~~~~~~
├─ androidx.navigation.NavHostController instance
│    Leaking: UNKNOWN
│    ↓ NavHostController.mOnDestinationChangedListeners
│                        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
├─ java.util.concurrent.CopyOnWriteArrayList instance
│    Leaking: UNKNOWN
│    ↓ CopyOnWriteArrayList.elements
│                           ~~~~~~~~
├─ java.lang.Object[] array
│    Leaking: UNKNOWN
│    ↓ Object[].[0]
│               ~~~
├─ androidx.navigation.ui.ToolbarOnDestinationChangedListener instance
│    Leaking: UNKNOWN
│    ↓ ToolbarOnDestinationChangedListener.mContext
│                                          ~~~~~~~~
├─ dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper instance
│    Leaking: UNKNOWN
│    ViewComponentManager$FragmentContextWrapper wraps an Activity with Activity.mDestroyed false
│    ↓ ViewComponentManager$FragmentContextWrapper.fragment
│                                                  ~~~~~~~~
╰→ com.mpierucci.android.hiltmemleak.LeakFragment instance
​     Leaking: YES (ObjectWatcher was watching this because com.mpierucci.android.hiltmemleak.LeakFragment received Fragment#onDestroy() callback and Fragment#mFragmentManager is null)
​     key = 257b183a-777d-43c3-a1b0-0d470d6d415c
​     watchDurationMillis = 19270
​     retainedDurationMillis = 14268

METADATA

Build.VERSION.SDK_INT: 28
Build.MANUFACTURER: Google
LeakCanary version: 2.4
App process name: com.mpierucci.android.hiltmemleak
Analysis duration: 7339 ms```

