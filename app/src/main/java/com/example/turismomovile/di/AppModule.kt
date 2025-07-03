package com.example.turismomovile.di


import com.example.turismomovile.data.local.SessionManager
import com.example.turismomovile.data.local.SettingsFactory
import com.example.turismomovile.data.remote.api.ApiConstants
import com.example.turismomovile.data.remote.api.base.AuthApiService
import com.example.turismomovile.data.remote.api.base.MenuApiService
import com.example.turismomovile.data.remote.api.configuracion.AsociacionApiService
import com.example.turismomovile.data.remote.api.configuracion.EmprendedorApiService
import com.example.turismomovile.data.remote.api.configuracion.ImgAsociacionesApiService
import com.example.turismomovile.data.remote.api.configuracion.ModuleApiService
import com.example.turismomovile.data.remote.api.configuracion.MunicipalidadApiService
import com.example.turismomovile.data.remote.api.configuracion.MunicipalidadDescriptionApiService
import com.example.turismomovile.data.remote.api.configuracion.ParentModuleApiService
import com.example.turismomovile.data.remote.api.configuracion.RoleApiService
import com.example.turismomovile.data.remote.api.configuracion.ServiceApiService
import com.example.turismomovile.data.remote.api.ventas.ReservaApiService
import com.example.turismomovile.data.repository.AuthRepositoryImpl
import com.example.turismomovile.data.repository.configuration.AsociacionesRepositoryImpl
import com.example.turismomovile.data.repository.configuration.ModuleRepositoryImpl
import com.example.turismomovile.data.repository.configuration.MunicipalidadRepositoryImpl
import com.example.turismomovile.data.repository.configuration.ParentModuleRepositoryImpl
import com.example.turismomovile.data.repository.configuration.RoleRepositoryImpl
import com.example.turismomovile.domain.repository.AuthRepository
import com.example.turismomovile.domain.repository.configuration.AsociacionesRepository
import com.example.turismomovile.domain.repository.configuration.ImgAsociacionesRepository
import com.example.turismomovile.domain.repository.configuration.ModuleRepository
import com.example.turismomovile.domain.repository.configuration.MunicipalidadRepository
import com.example.turismomovile.domain.repository.configuration.ParentModuleRepository
import com.example.turismomovile.domain.repository.configuration.RoleRepository
import com.example.turismomovile.domain.usecase.LoginUseCase
import com.example.turismomovile.domain.usecase.RegisterUseCase
import com.example.turismomovile.presentation.screens.configuration.ad.asociaciones.AsociacionesViewModel
import com.example.turismomovile.presentation.screens.configuration.ad.modules.ModuleViewModel
import com.example.turismomovile.presentation.screens.configuration.ad.modulos_padres.ParentModuleViewModel
import com.example.turismomovile.presentation.screens.configuration.ad.municipalidad.MunicipalidadDescriptionViewModel
import com.example.turismomovile.presentation.screens.configuration.ad.municipalidad.MunicipalidadViewModel
import com.example.turismomovile.presentation.screens.configuration.ad.role.RoleViewModel
import com.example.turismomovile.presentation.screens.configuration.ad.service.ServiceViewModel
import com.example.turismomovile.presentation.screens.dashboard.HomeViewModel
import com.example.turismomovile.presentation.screens.login.LoginViewModel
import com.example.turismomovile.presentation.theme.ThemeViewModel
import io.dev.kmpventas.data.repository.configuration.ImgAsociacionesRepositoryImpl
import com.example.turismomovile.presentation.screens.land_page.LangPageViewModel
import com.example.turismomovile.presentation.screens.land_page.ReservaViewModel
import com.example.turismomovile.presentation.screens.login.ProfileViewModel
import com.example.turismomovile.presentation.screens.login.RegisterViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {

    // =====================================
    // Settings & SessionManager con DataStore
    // =====================================

    single { SettingsFactory(get()).createSettings() } // DataStore<Preferences>
    single { SessionManager(get()) }



    // =====================================
    // HTTP Client
    // =====================================
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            defaultRequest {
                url(ApiConstants.BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
    }

    // =====================================
    // API Services
    // =====================================
    single { AuthApiService(get(), get()) }
    single { MenuApiService(get(),get()) }
    single { RoleApiService(get(),get()) }
    single { ModuleApiService(get(),get()) }
    single { ParentModuleApiService(get(),get()) }
    single { MunicipalidadDescriptionApiService(get(),get()) }
    single { MunicipalidadApiService(get(),get()) }
    single { ServiceApiService(get(),get()) }
    single { AsociacionApiService(get(),get()) }
    single { ImgAsociacionesApiService(get(),get()) }
    single { EmprendedorApiService (get(),get())}
    single { ReservaApiService (get(),get())}


    // =====================================
    // Repositories
    // =====================================
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<RoleRepository> { RoleRepositoryImpl(get()) }
    single<ModuleRepository> { ModuleRepositoryImpl(get()) }
    single<ParentModuleRepository> { ParentModuleRepositoryImpl(get()) }
    single<MunicipalidadRepository> { MunicipalidadRepositoryImpl(get()) }
    single<AsociacionesRepository> { AsociacionesRepositoryImpl(get()) }
    single<ImgAsociacionesRepository> { ImgAsociacionesRepositoryImpl(get()) }

    // =====================================
    // UseCases
    // =====================================
    single { LoginUseCase(get()) }
    single { RegisterUseCase(get(),get()) }


    // =====================================
    // ViewModels
    // =====================================
    viewModel { ThemeViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { LangPageViewModel(get(), get(), get(),get(),get()) }
    viewModel { RoleViewModel(get(), get(), get()) }
    viewModel { ModuleViewModel(get(), get()) }
    viewModel { ParentModuleViewModel(get()) }
    viewModel { MunicipalidadDescriptionViewModel(get()) }
    viewModel { MunicipalidadViewModel(get()) }
    viewModel { AsociacionesViewModel(get(), get(), get()) }
    viewModel {ServiceViewModel(get())}
    viewModel {ProfileViewModel(get())}
    viewModel {ReservaViewModel(get())}
    viewModel { RegisterViewModel(get()) }

}