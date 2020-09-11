package com.blackbox.plog.pLogs.workers

import android.content.Context
import androidx.annotation.Keep
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.blackbox.plog.mqtt.MQTTSender
import com.blackbox.plog.mqtt.PLogMQTTProvider
import com.blackbox.plog.mqtt.client.PahoMqqtClient
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@Keep
class LogsPublishWorker(appContext: Context, workerParams: WorkerParameters) :
        RxWorker(appContext, workerParams) {

    companion object {
        private val TAG = "LogsPublishWorker"
        val KEY_LOG_MESSAGE = "log_message"
    }

    override fun createWork(): Single<Result> {

        //Send Pending reports
        return Single.create {
            doWork(it)
        }
    }

    private fun doWork(emitter: SingleEmitter<Result>) {
        PahoMqqtClient.instance?.setConnected()

        try {
            val message = inputData.getString(KEY_LOG_MESSAGE)
            message?.let {
                if (PLogMQTTProvider.mqttEnabled && PLogMQTTProvider.topic.isNotEmpty() && message.isNotEmpty()) {
                    MQTTSender.sendMessage(message, context = applicationContext)
                            ?.subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.delay(1, TimeUnit.SECONDS)
                            ?.retryWhen(RetryWithDelay(2, 5000))
                            ?.subscribeBy(
                                    onNext = {
                                        if (it) {
                                            MQTTSender.doOnMessageDelivered()
                                            MQTTSender.printMQTTMessagesSummary("sentOnRetry")
                                            emitter.onSuccess(Result.success())
                                        }
                                    },
                                    onError = {
                                        it.printStackTrace()
                                    },
                                    onComplete = { }
                            )
                }
            }

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}
