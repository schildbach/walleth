package org.walleth.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_text.*
import org.kethereum.crypto.signMessage
import org.kethereum.extensions.toHexStringZeroPadded
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.walleth.R
import org.walleth.data.DEFAULT_PASSWORD
import org.walleth.data.keystore.WallethKeyStore
import org.walleth.data.networks.CurrentAddressProvider
import org.walleth.khex.toHexString

class SignTextActivity : AppCompatActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val keyStore: WallethKeyStore by instance()
    private val currentAddressProvider: CurrentAddressProvider by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_text)
        val text = intent.getStringExtra("TEXT")

        textToSign.text = text

        fab.setOnClickListener {
            val address = currentAddressProvider.getCurrent()
            val byteArray = text.toByteArray()
            val message = ("\u0019Ethereum Signed Message:\n" + byteArray.size).toByteArray() + byteArray

            val signature = keyStore.getKeyForAddress(address, DEFAULT_PASSWORD)?.signMessage(message)

            val rHEX = signature?.r?.toHexStringZeroPadded(64, false)
            val sHEX = signature?.s?.toHexStringZeroPadded(64, false)
            val v = signature?.v

            val signatureHex = (rHEX + sHEX + v?.toHexString())
            setResult(Activity.RESULT_OK, Intent().putExtra("SIGNATURE", signatureHex).putExtra("ADDRESS", address.cleanHex))
            finish()
        }

    }

}
