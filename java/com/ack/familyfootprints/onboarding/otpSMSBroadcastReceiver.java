package com.ack.familyfootprints.onboarding;

public class otpSMSBroadcastReceiver {
    private var listener: Listener? = null

    fun injectListener(listener: Listener?) {
        this.listener = listener
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {

                CommonStatusCodes.SUCCESS -> {

                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String

                    val pattern = Pattern.compile("\\d{6}")
                    val matcher = pattern.matcher(message)

                    if (matcher.find())
                        listener?.onSMSReceived(matcher.group(0))
                }
                CommonStatusCodes.TIMEOUT -> listener?.onTimeOut()
            }
        }
    }

    interface Listener {
        fun onSMSReceived(otp: String)
        fun onTimeOut()
    }
}

