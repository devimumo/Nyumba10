package com.example.nyumba10.AppPermissions

import android.content.pm.PackageManager

var permission_return_value=0

class AppPermissions {

         fun onRequestPermissionsResult(requestCode: Int,
                                                permissions: Array<String>, grantResults: IntArray): Int {
            when (requestCode) {
                1 -> {
                    // If request is cancelled, the result arrays are empty.
                    if ((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        // Permission is granted. Continue the action or workflow
                        // in your app.

                        permission_return_value=1
                    } else {
                        // Explain to the user that the feature is unavailable because
                        // the features requires a permission that the user has denied.
                        // At the same time, respect the user's decision. Don't link to
                        // system settings in an effort to convince the user to change
                        // their decision.
                        permission_return_value=0

                    }
                }

                // Add other 'when' lines to check for other
                // permissions this app might request.
                else -> {
                    // Ignore all other requests.
                }
            }

             return  permission_return_value

         }
    }

