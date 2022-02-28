# Hilt-Navigation-Leak
Project to reproduce a memory leak when using Hilt and Navigation Component reported [Here](https://github.com/google/dagger/issues/2070)

## Set Up

The project has 4 fragments hooked up thorugh 	[Navigaiton Component](https://developer.android.com/guide/navigation)

* MainFragment is the start navigation and lets you navigate to the other fragments.

* LeakFragment is marked with `@AndroidEnryPoint` so it can be injected with Hilt. It also updates the toolbar through the navigation component as suggested in the [official docs](https://developer.android.com/guide/navigation/navigation-ui).
As the name suggest, this fragment introduce the memory leak.

* NavigationOnlyFragment updates the toolbar thorugh the navigation compoenent, but does not mark the fragment as an entry point for hilt, hence there is no memory leak.

The LeakFragment, NavigationOnlyFragment and HiltOnlyFragment have the android:theme attribute defined in their xml layout files, addionally they access the layout inflater from a view’s context which causes the leak in LeakFragment but not in NavigationOnlyFragment & HiltOnlyFragment


## How to reproduce

Simply navigate to the `LeakFragment`and back a few times. Then wait until leak canary reports the leak.

## Leak

```
====================================
HEAP ANALYSIS RESULT
====================================
1 APPLICATION LEAKS

References underlined with "~~~" are likely causes.
Learn more at https://squ.re/leaks.

2371 bytes retained by leaking objects
Signature: 1a0ede954e6f91469419c087a7a63d305942c8e3
┬───
│ GC Root: Input or output parameters in native code
│
├─ dalvik.system.PathClassLoader instance
│    Leaking: NO (InternalLeakCanary↓ is not leaking and A ClassLoader is never leaking)
│    ↓ ClassLoader.runtimeInternalObjects
├─ java.lang.Object[] array
│    Leaking: NO (InternalLeakCanary↓ is not leaking)
│    ↓ Object[73]
├─ leakcanary.internal.InternalLeakCanary class
│    Leaking: NO (MainActivity↓ is not leaking and a class is never leaking)
│    ↓ static InternalLeakCanary.resumedActivity
├─ com.mpierucci.android.hiltmemleak.MainActivity instance
│    Leaking: NO (NavHostFragment↓ is not leaking and Activity#mDestroyed is false)
│    mApplication instance of com.mpierucci.android.hiltmemleak.App
│    mBase instance of androidx.appcompat.view.ContextThemeWrapper
│    ↓ FragmentActivity.mFragments
├─ androidx.fragment.app.FragmentController instance
│    Leaking: NO (NavHostFragment↓ is not leaking)
│    ↓ FragmentController.mHost
├─ androidx.fragment.app.FragmentActivity$HostCallbacks instance
│    Leaking: NO (NavHostFragment↓ is not leaking)
│    this$0 instance of com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    mActivity instance of com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    mContext instance of com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    ↓ FragmentHostCallback.mFragmentManager
├─ androidx.fragment.app.FragmentManagerImpl instance
│    Leaking: NO (NavHostFragment↓ is not leaking)
│    ↓ FragmentManager.mPrimaryNav
├─ androidx.navigation.fragment.NavHostFragment instance
│    Leaking: NO (Fragment#mFragmentManager is not null)
│    ↓ NavHostFragment.navHostController
│                      ~~~~~~~~~~~~~~~~~
├─ androidx.navigation.NavHostController instance
│    Leaking: UNKNOWN
│    Retaining 10.6 kB in 339 objects
│    activity instance of com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    context instance of com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    ↓ NavController.onDestinationChangedListeners
│                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
├─ java.util.concurrent.CopyOnWriteArrayList instance
│    Leaking: UNKNOWN
│    Retaining 4.5 kB in 156 objects
│    ↓ CopyOnWriteArrayList[0]
│                          ~~~
├─ androidx.navigation.ui.ToolbarOnDestinationChangedListener instance
│    Leaking: UNKNOWN
│    Retaining 4.5 kB in 153 objects
│    context instance of android.view.ContextThemeWrapper, wrapping activity com.mpierucci.android.hiltmemleak.
│    MainActivity with mDestroyed = false
│    ↓ AbstractAppBarOnDestinationChangedListener.context
│                                                 ~~~~~~~
├─ android.view.ContextThemeWrapper instance
│    Leaking: UNKNOWN
│    Retaining 4.0 kB in 142 objects
│    mBase instance of dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper, wrapping
│    activity com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    ContextThemeWrapper wraps an Activity with Activity.mDestroyed false
│    ↓ ContextThemeWrapper.mInflater
│                          ~~~~~~~~~
├─ com.android.internal.policy.PhoneLayoutInflater instance
│    Leaking: UNKNOWN
│    Retaining 3.9 kB in 134 objects
│    mContext instance of android.view.ContextThemeWrapper, wrapping activity com.mpierucci.android.hiltmemleak.
│    MainActivity with mDestroyed = false
│    mPrivateFactory instance of com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
│    ↓ LayoutInflater.mFactory
│                     ~~~~~~~~
├─ android.view.LayoutInflater$FactoryMerger instance
│    Leaking: UNKNOWN
│    Retaining 3.8 kB in 132 objects
│    ↓ LayoutInflater$FactoryMerger.mF1
│                                   ~~~
├─ androidx.fragment.app.FragmentLayoutInflaterFactory instance
│    Leaking: UNKNOWN
│    Retaining 3.8 kB in 131 objects
│    ↓ FragmentLayoutInflaterFactory.mFragmentManager
│                                    ~~~~~~~~~~~~~~~~
├─ androidx.fragment.app.FragmentManagerImpl instance
│    Leaking: UNKNOWN
│    Retaining 3.8 kB in 130 objects
│    ↓ FragmentManager.mOnAttachListeners
│                      ~~~~~~~~~~~~~~~~~~
├─ java.util.concurrent.CopyOnWriteArrayList instance
│    Leaking: UNKNOWN
│    Retaining 2.4 kB in 85 objects
│    ↓ CopyOnWriteArrayList[0]
│                          ~~~
├─ androidx.fragment.app.FragmentManager$6 instance
│    Leaking: UNKNOWN
│    Retaining 2.4 kB in 82 objects
│    Anonymous class implementing androidx.fragment.app.FragmentOnAttachListener
│    ↓ FragmentManager$6.val$parent
│                        ~~~~~~~~~~
╰→ com.mpierucci.android.hiltmemleak.LeakFragment instance
     Leaking: YES (ObjectWatcher was watching this because com.mpierucci.android.hiltmemleak.LeakFragment received
     Fragment#onDestroy() callback and Fragment#mFragmentManager is null)
     Retaining 2.4 kB in 81 objects
     key = a7b9ff79-3226-4f9f-9e4a-a156646c36d3
     watchDurationMillis = 440094
     retainedDurationMillis = 435093
     componentContext instance of dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper,
     wrapping activity com.mpierucci.android.hiltmemleak.MainActivity with mDestroyed = false
====================================
METADATA

Build.VERSION.SDK_INT: 30
Build.MANUFACTURER: Google
LeakCanary version: 2.8.1
App process name: com.mpierucci.android.hiltmemleak
Stats: LruCache[maxSize=3000,hits=35223,misses=91093,hitRate=27%]
RandomAccess[bytes=4209947,reads=91093,travel=23056462968,range=18577805,size=24376467]
Analysis duration: 6280 ms
Heap dump file path: /storage/emulated/0/Download/leakcanary-com.mpierucci.android.hiltmemleak/2022-02-28_18-40-07_039.
hprof
Heap dump timestamp: 1646070017699
Heap dump duration: Unknown
====================================```

