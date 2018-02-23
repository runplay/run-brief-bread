package run.brief.b;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import run.brief.bread.R;
import run.brief.news.NewsChooseFeedsFragment;
import run.brief.news.NewsHomeFragment;
import run.brief.news.ViewNewsItemFragment;
import run.brief.news.ViewNewsItemWebFragment;
import run.brief.search.SearchFragment;
import run.brief.settings.SettingsHomeTabbedFragment;

public final class Bgo {

    private static Activity useActivity;

    public static void setUseActivity(Activity activity) {
        useActivity=activity;
    }


 	public static void openCurrentState(Activity activity) {
		Class<? extends BFragment> fragment = null;

		//BLog.e("STATE", "openCurrentState: " + State.getCurrentSection() + ", size: " + State.getSectionsSize());
		switch (State.getCurrentSection()) {
		//case State.SECTION_TWITTER:
		//	fragment = new TwitterHomeFragment();
		//	break;
			case State.SECTION_NEWS:
				fragment = NewsHomeFragment.class;
				break;
			case State.SECTION_NEWS_CHOOSE:
				fragment = NewsChooseFeedsFragment.class;
				break;
			case State.SECTION_NEWS_VIEW:
				fragment = ViewNewsItemFragment.class;
				break;
			case State.SECTION_SETTINGS:
				fragment = SettingsHomeTabbedFragment.class;
				break;
			case State.SECTION_SEARCH:
				fragment = SearchFragment.class;
				break;
			case State.SECTION_NEWS_WEBVIEW:
				fragment = ViewNewsItemWebFragment.class;
				break;
			default:
				fragment = NewsHomeFragment.class;
				break;
		}
		if (fragment != null) {
			Bgo.openFragment(activity, fragment);
		}
	}

    //@SuppressWarnings()
	public static boolean openFragment(Activity activity, Class<? extends Fragment> fragment) {
		setUseActivity(activity);
		Device.hideKeyboard(activity);
		State.sectionsGoBackstack();
		FragmentManager fm = activity.getFragmentManager();

		FragmentTransaction tr = fm.beginTransaction();
		tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tr.replace(R.id.container, Fragment.instantiate(activity, fragment.getName()));
		tr.commit();

		return true;
	}

	/*
	public static boolean openPopFragment(Activity activity, Class<? extends Fragment> fragment) {


		//if(!activity.isDestroyed()) {
		setUseActivity(activity);

		Device.hideKeyboard(activity);
		State.sectionsGoBackstack();
		FragmentManager fm = activity.getFragmentManager();

		FragmentTransaction tr = fm.beginTransaction();
		tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tr.add(R.id.container, Fragment.instantiate(activity, fragment.getName()));
		// tr.replace(R.id.container, fragment, fragment.getClass().getName());
		//tr.replace(R.id.container, fragment, fragment.getClass().getName());

		tr.commit();
		//}
		//Log.e("BGO", "openfrgament: " + fragment.getClass().getName() + " - backstack: " + fm.getBackStackEntryCount());
		return true;
	}
	*/
	public static boolean openFragmentAnimate(Activity activity, Fragment fragment) {
        setUseActivity(activity);

		Device.hideKeyboard(activity);
		State.sectionsGoBackstack();
		FragmentManager fm = activity.getFragmentManager();
		FragmentTransaction tr = fm.beginTransaction();
        tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tr.replace(R.id.container, fragment, fragment.getClass().getName());
		tr.commit();
		return true;
	}

	public static boolean openFragmentBackStackAnimate(Activity activity,Fragment fragment) {
        setUseActivity(activity);
		Device.hideKeyboard(activity);

		FragmentManager fm = activity.getFragmentManager();
		FragmentTransaction tr = fm.beginTransaction();
        tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tr.replace(R.id.container, fragment, fragment.getClass().getName());

		tr.commit();
		return true;
	}

	public static boolean openFragmentBackStack(Activity activity,Fragment fragment) {

        setUseActivity(activity);
		Device.hideKeyboard(activity);

		FragmentManager fm = activity.getFragmentManager();
		FragmentTransaction tr = fm.beginTransaction();
        tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        
		tr.replace(R.id.container, fragment, fragment.getClass().getName());
		tr.commit();
		return true;
	}

	public static void clearBackStack(Activity activity) {
		FragmentManager fm = activity.getFragmentManager();
		int backStackCount = fm.getBackStackEntryCount();
		for (int i = 0; i < backStackCount; i++) {
			int backStackId = fm.getBackStackEntryAt(i).getId();

			fm.popBackStack(backStackId,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);

		}
		State.sectionsClearBackstack();
	}

	public static void refreshFragment(Activity activity,String fragmentClassName) {
		FragmentManager fm = activity.getFragmentManager();
		try {
			BRefreshable f = (BRefreshable) fm
					.findFragmentByTag(fragmentClassName);
			if (f != null) {
				f.refresh();
			}
		} catch (Exception e) {

		}
	}


    public static void tryRefreshCurrentFragment() {
        if (useActivity != null) {
			//BLog.e("TRY", "********************** activity=ok");
            refreshCurrentFragment(useActivity);
        } else {
           //BLog.e("TRY", "********************** activity=null");
        }
    }
    public static void tryRefreshDataCurrentFragment() {
        if (useActivity != null) {
            refreshDataCurrentFragment(useActivity);
        }
    }
    public static void tryRefreshCurrentIfFragment(Class ifRefreshableClass) {
        if (useActivity != null) {
            refreshCurrentIfFragment(useActivity, ifRefreshableClass);
        }
    }
    public static void refreshDataCurrentIfFragment(Class ifRefreshableClass) {
        if (useActivity != null) {

            final BRefreshable f = getCurrentRefeshableFragment(useActivity);
            //BLog.e("REFC","current: "+f.getClass().getName()+" -- need match : "+ifRefreshableClass.getName());
            if (f != null && f.getClass().getName().equals(ifRefreshableClass.getName())) {
                useActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        f.refreshData();
                    }
                });
            }

        }
    }
	public static void refreshCurrentFragment(Activity activity) {
		if (activity != null) {

			BRefreshable f = getCurrentRefeshableFragment(activity);
			if (f != null) {
				//BLog.e("TRY", "******refreshing........");
				f.refresh();
			}

		}
	}
    public static void refreshDataCurrentFragment(Activity activity) {
        if (activity != null) {

            BRefreshable f = getCurrentRefeshableFragment(activity);
            if (f != null) {
                f.refreshData();
            }

        }
    }
    public static void refreshDataCurrentIfFragment(Activity activity,Class ifRefreshableClass) {
        if (activity != null) {

            BRefreshable f = getCurrentRefeshableFragment(activity);
            if (f != null && f.getClass().getName().equals(ifRefreshableClass.getName())) {
                f.refreshData();
            }

        }
    }
	public static void refreshCurrentIfFragment(Activity activity,Class ifRefreshableClass) {
		if (activity != null) {

			final BRefreshable f = getCurrentRefeshableFragment(activity);
            //BLog.e("REFC","current: "+f.getClass().getName()+" -- need match : "+ifRefreshableClass.getName());
			if (f != null && f.getClass().getName().equals(ifRefreshableClass.getName())) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						f.refresh();
					}
				});
			}

		}
	}

	public static BRefreshable getCurrentRefeshableFragment(Activity activity) {
		if (activity != null) {
			FragmentManager fm = activity.getFragmentManager();
			try {

				BRefreshable f = (BRefreshable) fm
						.findFragmentByTag(getFragmentNameBystate(State.getCurrentSection()));
                //BLog.e("REF","---"+getFragmentNameBystate(State.getCurrentSection()));
				if (f != null) {
					return f;
				} else {
                    BRefreshable fr = (BRefreshable) fm
                            .findFragmentById(R.id.container);
                    if (fr != null) {
                        return fr;
                    }
                }
			} catch (Exception e) {
               // BLog.e("REF","ex: "+e.toString());
			}
		}
		return null;
	}
	public static Fragment getCurrentFragment(Activity activity) {
		if (activity != null) {
			FragmentManager fm = activity.getFragmentManager();
			try {

				Fragment f = (Fragment) fm
						.findFragmentByTag(getFragmentNameBystate(State.getCurrentSection()));
				//BLog.e("REF","---"+getFragmentNameBystate(State.getCurrentSection()));
				if (f != null) {
					return f;
				}
			} catch (Exception e) {
				// BLog.e("REF","ex: "+e.toString());
			}
		}
		return null;
	}
    public static void removeFragmentFromFragmentManager(Activity activity, String TAG_FRAGMENT) {
        if (activity != null) {
            Fragment fragment = activity.getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
            if(fragment != null)
                activity.getFragmentManager().beginTransaction().remove(fragment).commit();


        }
    }
	private static String getFragmentNameBystate(int STATE_) {
		String fragname = null;

		switch (STATE_) {

        case State.SECTION_SEARCH:
            fragname = SearchFragment.class.getName();
            break;
		case State.SECTION_NEWS:
			fragname = NewsHomeFragment.class.getName();
			break;
		case State.SECTION_NEWS_CHOOSE:
			fragname = NewsChooseFeedsFragment.class.getName();
			break;
		case State.SECTION_NEWS_VIEW:
			fragname = ViewNewsItemFragment.class.getName();
			break;
		case State.SECTION_NEWS_WEBVIEW:
			fragname = ViewNewsItemWebFragment.class.getName();
			break;
		default:
			fragname = NewsHomeFragment.class.getName();
			break;


		}
		//Log.e("ST", "GET getFragmentNameBystate(): "+fragname);
		return fragname;
	}

	public static void goPreviousFragment(Activity activity) {
		Device.hideKeyboard(activity);
//BLog.e("SS", "Go previous sections size: " + State.getSectionsSize());
		if (State.getSectionsSize() == 0) {
			Bgo.clearBackStack(activity);
			Bgo.openFragment(activity, NewsHomeFragment.class);
		} else {
			State.sectionsGoBackstack();
			Bgo.openCurrentState(activity);

		}

	}

}
