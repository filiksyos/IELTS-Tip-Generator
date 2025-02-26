package com.example.presentation.presentationTest

import com.example.presentation.DI.appModule
import org.koin.test.check.checkModules
import kotlin.test.Test

class koinTest{

    @Test
    fun `verify Koin module configuration`() {
        checkModules {
            modules(appModule)
        }
    }

}

