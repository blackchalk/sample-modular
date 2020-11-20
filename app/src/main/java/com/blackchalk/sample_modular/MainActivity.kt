package com.blackchalk.sample_modular

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	/**
	 * SplitInstallManager = is responsible for downloading the module.
	 * The app has to be in Foreground to download the dynamic module.
	 * SplitInstallRequest = will contain the request information that
	 * will be used to request our dynamic feature module from Google Play.
	 */
	lateinit var splitInstallManager: SplitInstallManager
	lateinit var request: SplitInstallRequest
	val DYNAMIC_FEATURE = "news_feature"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

		initDynamicModules()
		setClickListeners()

    }

	private fun downloadFeature() {
		// Initializes a variable to later track the session ID for a given request.
		var mySessionId = 0

		// Creates a listener for request status updates.
		val listener = SplitInstallStateUpdatedListener { state ->
			if (state.sessionId() == mySessionId) {
				// Read the status of the request to handle the state update.
				Log.e("READ", state.sessionId().toString() )
			}
		}

		// Registers the listener.
		splitInstallManager.registerListener(listener)
		splitInstallManager.startInstall(request)
			// When the platform accepts your request to download
			// an on demand module, it binds it to the following session ID.
			// You use this ID to track further status updates for the request.
			.addOnFailureListener {
				// Handle request errors.
			}
			// When the platform accepts your request to download
			// an on demand module, it binds it to the following session ID.
			// You use this ID to track further status updates for the request.
			.addOnSuccessListener {

					sessionId -> mySessionId = sessionId

					buttonOpenNewsModule.visibility = View.VISIBLE
					buttonDeleteNewsModule.visibility = View.VISIBLE
				Log.e("READ Success", "no internet" )
				}
			.addOnFailureListener { exception ->
				// You should also add the following listener to handle any errors
				// processing the request.
				when ((exception as SplitInstallException).errorCode) {
					SplitInstallErrorCode.NETWORK_ERROR -> {
						// Display a message that requests the user to establish a
						// network connection.
						Log.e("READ Failure", "no internet" )

					}
					SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED -> checkForActiveDownloads()
					// still has ongoing sessions, notify user
				}
			}

		// When your app no longer requires further updates, unregister the listener.
		splitInstallManager.unregisterListener(listener)
	}

	fun checkForActiveDownloads() {
		splitInstallManager
			// Returns a SplitInstallSessionState object for each active session as a List.
			.sessionStates
			.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					// Check for active sessions.
					for (state in task.result) {
						if (state.status() == SplitInstallSessionStatus.DOWNLOADING) {
							Log.e("READ active sessions", state.status().toString())
							// Cancel the request, or request a deferred installation.
						}
					}
				}
			}
	}

	private fun initDynamicModules() {

		splitInstallManager = SplitInstallManagerFactory.create(this)
		request = SplitInstallRequest
			.newBuilder()
			.addModule(DYNAMIC_FEATURE)
			.build();
	}

	private fun setClickListeners() {

		/**
		 * uses kotlin synthetic, 'kotlin-android-extensions'
		 * just apply plugin at app's build.gradle
		 */
		buttonClick.setOnClickListener {
			if (!isDynamicFeatureDownloaded(DYNAMIC_FEATURE)) {
				downloadFeature()
			} else {
				buttonDeleteNewsModule.visibility = View.VISIBLE
				buttonOpenNewsModule.visibility = View.VISIBLE
			}
		}

		buttonOpenNewsModule.setOnClickListener {
			val intent = Intent().setClassName(this, "com.blackchalk.news_feature.NewsLoaderActivity")
			startActivity(intent)
		}

		/**
		 * And now, to delete the downloaded dynamic-feature-module in Android
		 */
		buttonDeleteNewsModule.setOnClickListener {
			val list = ArrayList<String>()
			list.add(DYNAMIC_FEATURE)
			uninstallDynamicFeature(list)

			/**
			 * If we have multiple modules in the app, we can get the list of all the installed modules using,
			SplitInstallManager.getInstalledModules()
			 */
		}
	}

	private fun isDynamicFeatureDownloaded(feature: String): Boolean =
		splitInstallManager.installedModules.contains(feature)


	private fun uninstallDynamicFeature(list: List<String>) {
		splitInstallManager.deferredUninstall(list)
			.addOnSuccessListener {
				buttonDeleteNewsModule.visibility = View.GONE
				buttonOpenNewsModule.visibility = View.GONE
			}
	}
}
