package com.example.turismomovile.data.remote.api

/*import io.dev.kmpventas.data.remote.dto.product.Category
import io.dev.kmpventas.data.remote.dto.product.CategoryResponse
import com.example.turismomovile.data.remote.dto.LoginDTO
import com.example.turismomovile.data.remote.dto.LoginResponse
import com.example.turismomovile.data.remote.dto.MenuItem
import com.example.turismomovile.data.remote.dto.configuracion.ModuleDTO
import com.example.turismomovile.data.remote.dto.configuracion.ModuleResponse
import com.example.turismomovile.data.remote.dto.configuracion.ModuleSelectedDTO
import io.dev.kmpventas.data.remote.dto.product.Product
import io.dev.kmpventas.data.remote.dto.product.ProductResponse
import com.example.turismomovile.data.remote.dto.RefreshTokenDTO
import com.example.turismomovile.data.remote.dto.configuracion.Role
import com.example.turismomovile.data.remote.dto.configuracion.RoleResponse
import io.dev.kmpventas.data.remote.dto.product.UnitMeasurement
import io.dev.kmpventas.data.remote.dto.product.UnitMeasurementResponse
import io.dev.kmpventas.data.remote.dto.accounting.AccountingAccountClass
import io.dev.kmpventas.data.remote.dto.accounting.AccountingDynamics
import io.dev.kmpventas.data.remote.dto.accounting.AccountingDynamicsResponse
import io.dev.kmpventas.data.remote.dto.accounting.AccountingPlan
import io.dev.kmpventas.data.remote.dto.accounting.Area
import io.dev.kmpventas.data.remote.dto.accounting.SerialFlow
import io.dev.kmpventas.data.remote.dto.accounting.SerialFlowCreateRequest
import io.dev.kmpventas.data.remote.dto.accounting.SerialFlowResponse
import io.dev.kmpventas.data.remote.dto.accounting.Store
import io.dev.kmpventas.data.remote.dto.accounting.StoreResponse
import io.dev.kmpventas.data.remote.dto.accounting.TypeAffectation
import io.dev.kmpventas.data.remote.dto.accounting.TypeDocument
import io.dev.kmpventas.data.remote.dto.configuracion.Company
import io.dev.kmpventas.data.remote.dto.configuracion.CompanyRegistrationRequest
import io.dev.kmpventas.data.remote.dto.configuracion.CompanyResponse
import io.dev.kmpventas.data.remote.dto.configuracion.ForgotPasswordRequest
import io.dev.kmpventas.data.remote.dto.configuracion.KeycloakUser
import com.example.turismomovile.data.remote.dto.configuracion.ModuleCreateDTO
import com.example.turismomovile.data.remote.dto.configuracion.ParentModule
import com.example.turismomovile.data.remote.dto.configuracion.ParentModuleDetail
import com.example.turismomovile.data.remote.dto.configuracion.ParentModuleListResponse
import io.dev.kmpventas.data.remote.dto.configuracion.ResetPasswordRequest
import com.example.turismomovile.data.remote.dto.configuracion.RoleCompanyUser
import com.example.turismomovile.data.remote.dto.configuracion.UserRepresentation
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex


class ApiService(private val client: HttpClient) {
    private var _authToken: String? = null
    val authToken: String? get() = _authToken
    private var _refreshToken: String? = null

    private val refreshMutex = Mutex()

    suspend fun login(loginDTO: LoginDTO): LoginResponse {
        val response = client.post(ApiConstants.LOGIN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(loginDTO)
        }
        val loginResponse = response.body<LoginResponse>()
        _authToken = loginResponse.access_token
        _refreshToken = loginResponse.refresh_token
        return loginResponse
    }

    suspend fun refreshToken(): LoginResponse {
        return _refreshToken?.let { refreshToken ->
            val response = client.post(ApiConstants.REFRESH_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenDTO(refreshToken))
            }
            val loginResponse = response.body<LoginResponse>()
            _authToken = loginResponse.access_token
            _refreshToken = loginResponse.refresh_token
            loginResponse
        } ?: throw IllegalStateException("No refresh token available")
    }

    suspend fun getMenuItems(): List<MenuItem> {
        return try {
            val menuItems: List<MenuItem> = client.get(ApiConstants.MENU_ENDPOINT) {
                _authToken?.let {
                    header(HttpHeaders.Authorization, "Bearer $it")
                } ?: throw IllegalStateException("No auth token available")
            }.body()
            if (menuItems.isEmpty()) {
            } else {
                menuItems.forEach { menuItem ->
                }
            }
            menuItems
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    fun setAuthToken(token: String?) {
        _authToken = token
    }
    fun setRefreshToken(token: String?) {
        _refreshToken = token
    }
    // *************************** CONFIGURACION INICIO APIS ***************************

    // KEYCLOAK USER

    suspend fun updateUser(userId: String, user: UserRepresentation): Result<Unit> {
        val userDto = user.copy(id = "")
        return try {
            client.put(ApiConstants.Configuration.UPDATE_USER.replace("{userId}", userId)) {
                contentType(ContentType.Application.Json)
                setBody(userDto)
                _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun createUser(user: UserRepresentation): UserRepresentation {
        return client.post(ApiConstants.Configuration.CREATE_USER) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }


    suspend fun createToken(): HttpResponse {
        return client.post(ApiConstants.Configuration.CREATE_TOKEN) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }

    suspend fun getUsers(page: Int = 0, size: Int = 20): List<CompanyResponse> {
        return client.get(ApiConstants.Configuration.GET_USER) {
            parameter("page", page)
            parameter("size", size)
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun searchUsers(): List<UserRepresentation> {
        return client.get(ApiConstants.Configuration.SEARCH_USER) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }
    suspend fun getCompanyUserRoles(idUser: String): List<RoleCompanyUser> {
        return client.get(ApiConstants.Configuration.COMPANY_USER_ROLE.replace("{idUser}", idUser)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }


    suspend fun getUserCompany(): List<KeycloakUser> {
        return client.get(ApiConstants.Configuration.GET_COMPANY) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }
    suspend fun searchUserByUsername(username: String): UserRepresentation {
        return client.get(ApiConstants.Configuration.SEARCH_USERNAME.replace("{username}", username)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun deleteUser(userId: String) {
        client.delete(ApiConstants.Configuration.DELETE_USER.replace("{userId}", userId)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }

    /// hasta aquiii

    // COMPANY USER

    suspend fun getAllCompanies(): List<Company> {
        return client.get(ApiConstants.Configuration.GET_ALL) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getCompanyByUuid(uuid: String): Company {
        return client.get(ApiConstants.Configuration.GET_BY_UUID.replace("{uuid}", uuid)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getCompanyToken(): Company {
        return client.get(ApiConstants.Configuration.GET_TOKEN) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun verifyEmail(userId: String): HttpResponse {
        return client.post(ApiConstants.Configuration.VERIFY_EMAIL.replace("{userId}", userId)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    suspend fun resetPassword(request: ResetPasswordRequest): HttpResponse {
        return client.post(ApiConstants.Configuration.RESET_PASSWORD) {
            contentType(ContentType.Application.Json)
            setBody(request)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    suspend fun registerCompany(request: CompanyRegistrationRequest): Company {
        return client.post(ApiConstants.Configuration.REGISTRATION) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun forgotPassword(request: ForgotPasswordRequest): HttpResponse {
        return client.post(ApiConstants.Configuration.FORGOT_PASSWORD) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun updateCompany(request: Company): Company {
        return client.put(ApiConstants.Configuration.UPDATE_COMPANY) {
            contentType(ContentType.Application.Json)
            setBody(request)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }


    // ROLES
    suspend fun getRoles(page: Int = 0, size: Int = 20, name: String? = null): RoleResponse {
        return client.get(ApiConstants.Configuration.ROLES) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getRoleById(id: String): Role {
        return client.get(ApiConstants.Configuration.ROLE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun createRole(role: Role): Role {
        return client.post(ApiConstants.Configuration.ROLES) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(role)
        }.body()
    }

    suspend fun updateRole(id: String, role: Role): Role {
        return client.put(ApiConstants.Configuration.ROLE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(role)
        }.body()
    }

    suspend fun deleteRole(id: String) {
        client.delete(ApiConstants.Configuration.ROLE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    // --- MODULES ---

    suspend fun getModules(page: Int = 0, size: Int = 20, name: String?): ModuleResponse {
        return client.get(ApiConstants.Configuration.MODULES) {
            parameter("page", page)
            parameter("size", size)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            } ?: throw IllegalStateException("Auth token is missing")
        }.body()
    }

    suspend fun getModuleById(id: String): ModuleDTO {
        return client.get(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            } ?: throw IllegalStateException("Auth token is missing")
        }.body()
    }

    suspend fun createModule(module: ModuleCreateDTO): ModuleDTO {
        return client.post(ApiConstants.Configuration.MODULES) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            } ?: throw IllegalStateException("Auth token is missing")
            contentType(ContentType.Application.Json)
            setBody(module) // ✅ Solo enviamos los datos necesarios para crear el módulo
        }.body()
    }

    suspend fun updateModule(id: String, module: ModuleCreateDTO): ModuleDTO {
        return client.put(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            } ?: throw IllegalStateException("Auth token is missing")
            contentType(ContentType.Application.Json)
            setBody(module)
        }.body()
    }

    suspend fun deleteModule(id: String) {
        client.delete(ApiConstants.Configuration.MODULE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            } ?: throw IllegalStateException("Auth token is missing")
        }
    }

    suspend fun getModulesSelected(roleId: String, parentModuleId: String): List<ModuleSelectedDTO> {
        val url = ApiConstants.Configuration.MODULE_SELECTED
            .replace("{roleId}", roleId)
            .replace("{parentModuleId}", parentModuleId)
        return client.get(url) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            } ?: throw IllegalStateException("Auth token is missing")
        }.body()
    }


    // MODULES PARENT

    suspend fun getParentModules(page: Int = 0, size: Int = 20, name: String? = null): ParentModuleListResponse {
        return client.get(ApiConstants.Configuration.GET_PARENT_MODULE) {
            parameter("page", page)
            parameter("size", size)
            name?.let { parameter("name", it) }
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getParentModuleById(id: String): ParentModule {
        return client.get(ApiConstants.Configuration.GET_PARENT_MODULE_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun createParentModule(parentModule: ParentModule): ParentModule {
        return client.post(ApiConstants.Configuration.CREATE_PARENT_MODULE) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(parentModule)
        }.body()
    }

    suspend fun updateParentModule(id: String, parentModule: ParentModule): ParentModule {
        return client.put(ApiConstants.Configuration.UPDATE_PARENT_MODULE.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(parentModule)
        }.body()
    }

    suspend fun deleteParentModule(id: String) {
        client.delete(ApiConstants.Configuration.DELETE_PARENT_MODULE.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    suspend fun getParentModuleList(): List<ParentModule> {
        return client.get(ApiConstants.Configuration.GET_PARENT_MODULE_LIST) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getParentModuleDetailList(): List<ParentModuleDetail> {
        return client.get(ApiConstants.Configuration.GET_PARENT_MODULE_DETAIL_LIST) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    // *************************** CONFIGURACION FIN APIS ***************************

    // *************************** PRODUCTOS INICIO APIS ***************************

    // Product
    suspend fun getProducts(page: Int = 0, size: Int = 10): ProductResponse {
        return client.get(ApiConstants.Catalog.PRODUCTS) {
            parameter("page", page)
            parameter("size", size)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getActiveProducts(): List<Product> {
        return client.get(ApiConstants.Catalog.PRODUCTS_ACTIVE) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getProductById(id: String): Product {
        return client.get(ApiConstants.Catalog.PRODUCTS_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun createProduct(product: Product): Product {
        return client.post(ApiConstants.Catalog.PRODUCTS) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(product)
        }.body()
    }

    suspend fun updateProduct(id: String, product: Product): Product {
        return client.put(ApiConstants.Catalog.PRODUCTS_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(product)
        }.body()
    }

    suspend fun deleteProduct(id: String) {
        client.delete(ApiConstants.Catalog.PRODUCTS_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    suspend fun getProductsByStockRange(minStock: Int, maxStock: Int): List<Product> {
        return client.get(ApiConstants.Catalog.PRODUCTS_STOCK) {
            parameter("minStock", minStock)
            parameter("maxStock", maxStock)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }


    //Category
    suspend fun getCategories(page: Int = 0, size: Int = 10): CategoryResponse {
        return client.get(ApiConstants.Catalog.CATEGORIES) {
            parameter("page", page)
            parameter("size", size)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getActiveCategories(): List<Category> {
        return client.get(ApiConstants.Catalog.CATEGORIES_ACTIVE) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun getCategoryById(id: String): Category {
        return client.get(ApiConstants.Catalog.CATEGORIES_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    suspend fun createCategory(category: Category): Category {
        return client.post(ApiConstants.Catalog.CATEGORIES) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(category)
        }.body()
    }

    suspend fun updateCategory(id: String, category: Category): Category {
        return client.put(ApiConstants.Catalog.CATEGORIES_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(category)
        }.body()
    }

    suspend fun deleteCategory(id: String) {
        client.delete(ApiConstants.Catalog.CATEGORIES_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    // UNIT MEASUREMENT
    suspend fun getUnitMeasurements(page: Int = 0, size: Int = 20): UnitMeasurementResponse {
        return client.get(ApiConstants.Catalog.UNIT_MEASUREMENTS) {
            parameter("page", page)
            parameter("size", size)
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }
    suspend fun getUnitMeasurementById(id: String): UnitMeasurement {
        return client.get(ApiConstants.Catalog.UNIT_MEASUREMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }.body()
    }

    // Obtener unidades de medida activas
    suspend fun getActiveUnitMeasurements(): List<UnitMeasurement> {
        return client.get(ApiConstants.Catalog.UNIT_MEASUREMENTS + "/active") {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    // Obtener unidad de medida por symbolPrint
    suspend fun getUnitMeasurementBySymbolPrint(symbolPrint: String): UnitMeasurement {
        return client.get(ApiConstants.Catalog.UNIT_MEASUREMENT_SYMBOL_PRINT.replace("{symbolPrint}", symbolPrint)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    // Obtener unidad de medida por sunatCode
    suspend fun getUnitMeasurementBySunatCode(sunatCode: String): UnitMeasurement {
        return client.get(ApiConstants.Catalog.UNIT_MEASUREMENT_SUNAT.replace("{sunatCode}", sunatCode)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun createUnitMeasurement(unitMeasurement: UnitMeasurement): UnitMeasurement {
        return client.post(ApiConstants.Catalog.UNIT_MEASUREMENTS) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(unitMeasurement)
        }.body()
    }
    suspend fun updateUnitMeasurement(id: String, unitMeasurement: UnitMeasurement): UnitMeasurement {
        return client.put(ApiConstants.Catalog.UNIT_MEASUREMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
            contentType(ContentType.Application.Json)
            setBody(unitMeasurement)
        }.body()
    }
    suspend fun deleteUnitMeasurement(id: String) {
        client.delete(ApiConstants.Catalog.UNIT_MEASUREMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let {
                header(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }

    // *************************** CATALOGO FIN APIS ***************************


   // *************************** CONTABILIDAD INICIO APIS ***************************
    // Areas
    suspend fun getAreas(): List<Area> {
        return client.get(ApiConstants.Accounting.AREAS) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }


    suspend fun getAreaById(id: String): Area {
        return client.get(ApiConstants.Accounting.AREA_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getAreaTree(): List<Area> {
        return client.get(ApiConstants.Accounting.AREA_TREE) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun createArea(area: Area): Area {
        return client.post(ApiConstants.Accounting.AREAS) {
            contentType(ContentType.Application.Json)
            setBody(area)
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun updateArea(id: String, area: Area): Area {
        return client.put(ApiConstants.Accounting.AREA_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(area)
        }.body()
    }

    suspend fun deleteArea(id: String) {
        client.delete(ApiConstants.Accounting.AREA_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }


    // Accounting Account Classes
    suspend fun getAccountingAccountClasses(): List<AccountingAccountClass> {
        return client.get(ApiConstants.Accounting.CLASSES_ACCOUNTING) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getAccountingAccountClassById(id: String): AccountingAccountClass {
        return client.get(ApiConstants.Accounting.CLASS_ACCOUNTING_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getActiveAccountingAccountClasses(): List<AccountingAccountClass> {
        return client.get(ApiConstants.Accounting.CLASS_ACCOUNTING_ACTIVE) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun createAccountingAccountClass(accountingAccountClass: AccountingAccountClass): AccountingAccountClass {
        return client.post(ApiConstants.Accounting.CLASSES_ACCOUNTING) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(accountingAccountClass)
        }.body()
    }

    suspend fun updateAccountingAccountClass(id: String, accountingAccountClass: AccountingAccountClass): AccountingAccountClass {
        return client.put(ApiConstants.Accounting.CLASS_ACCOUNTING_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(accountingAccountClass)
        }.body()
    }

    suspend fun deleteAccountingAccountClass(id: String) {
        client.delete(ApiConstants.Accounting.CLASS_ACCOUNTING_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }



    suspend fun getAccountingDynamics(page: Int = 0): AccountingDynamicsResponse {
        return client.get(ApiConstants.Accounting.DYNAMICS) {
            parameter("page", page) // Añadir paginación, si aplica
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getAccountingDynamicById(id: String): AccountingDynamics {
        return client.get(ApiConstants.Accounting.DYNAMICS_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun createAccountingDynamic(accountingDynamics: AccountingDynamics): AccountingDynamics {
        return client.post(ApiConstants.Accounting.DYNAMICS) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(accountingDynamics)
        }.body<AccountingDynamics>()
    }

    suspend fun updateAccountingDynamic(id: String, accountingDynamics: AccountingDynamics): AccountingDynamics {
        return client.put(ApiConstants.Accounting.DYNAMICS_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(accountingDynamics)
        }.body<AccountingDynamics>()
    }

    suspend fun deleteAccountingDynamic(id: String) {
        client.delete(ApiConstants.Accounting.DYNAMICS_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }


    // Type Documents
    suspend fun getTypeDocuments(): List<TypeDocument> {
        return client.get(ApiConstants.Accounting.DOCUMENTS) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getTypeDocumentById(id: String): TypeDocument {
        return client.get(ApiConstants.Accounting.DOCUMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun createTypeDocument(typeDocument: TypeDocument): TypeDocument {
        return client.post(ApiConstants.Accounting.DOCUMENTS) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(typeDocument)
        }.body()
    }

    suspend fun updateTypeDocument(id: String, typeDocument: TypeDocument): TypeDocument {
        return client.put(ApiConstants.Accounting.DOCUMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(typeDocument)
        }.body()
    }

    suspend fun deleteTypeDocument(id: String) {
        client.delete(ApiConstants.Accounting.DOCUMENT_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }

    // Accounting Plan
    suspend fun getAccountingPlans(): List<AccountingPlan> {
        return client.get(ApiConstants.Accounting.PLAN_ACCOUNTING) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getAccountingPlanById(id: String): AccountingPlan {
        return client.get(ApiConstants.Accounting.PLAN_ACCOUNTING_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun createAccountingPlan(accountingPlan: AccountingPlan): AccountingPlan {
        return client.post(ApiConstants.Accounting.PLAN_ACCOUNTING) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(accountingPlan)
        }.body()
    }

    suspend fun updateAccountingPlan(id: String, accountingPlan: AccountingPlan): AccountingPlan {
        return client.put(ApiConstants.Accounting.PLAN_ACCOUNTING_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(accountingPlan)
        }.body()
    }

    suspend fun deleteAccountingPlan(id: String) {
        client.delete(ApiConstants.Accounting.PLAN_ACCOUNTING_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }

    suspend fun getAccountingPlanTree(): List<AccountingPlan> {
        return client.get(ApiConstants.Accounting.PLAN_ACCOUNTING_TREE) { // 🔥 Aquí se usa el endpoint TREE
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }





    // Stores
    suspend fun getStores(page: Int = 0, size: Int = 10): StoreResponse {
        return client.get(ApiConstants.Accounting.STORE_SEARCH) {
            parameter("page", page)
            parameter("size", size)
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getStoreById(id: String): Store {
        return client.get(ApiConstants.Accounting.STORE_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun createStore(store: Store): Store {
        return client.post(ApiConstants.Accounting.STORES) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(store)
        }.body()
    }

    suspend fun updateStore(id: String, store: Store): Store {
        return client.put(ApiConstants.Accounting.STORE_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(store)
        }.body()
    }

    suspend fun deleteStore(id: String) {
        client.delete(ApiConstants.Accounting.STORE_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }


    // Type Affectations
    suspend fun getTypeAffectations(): List<TypeAffectation> {
        return client.get(ApiConstants.Accounting.AFFECTATIONS) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getTypeAffectationById(id: String): TypeAffectation {
        return client.get(ApiConstants.Accounting.AFFECTATION_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun createTypeAffectation(typeAffectation: TypeAffectation): TypeAffectation {
        return client.post(ApiConstants.Accounting.AFFECTATIONS) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(typeAffectation)
        }.body()
    }

    suspend fun updateTypeAffectation(id: String, typeAffectation: TypeAffectation): TypeAffectation {
        return client.put(ApiConstants.Accounting.AFFECTATION_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(typeAffectation)
        }.body()
    }

    suspend fun deleteTypeAffectation(id: String) {
        client.delete(ApiConstants.Accounting.AFFECTATION_BY_ID.replace("{id}", id)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }

    //SERIAL FLOWS

    suspend fun getSerialFlows(): SerialFlowResponse {
        return client.get(ApiConstants.Accounting.SERIAL_FLOWS) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body()
    }

    suspend fun getSerialFlowsByStore(storeId: String): List<SerialFlow> {
        return client.get(ApiConstants.Accounting.SERIAL_FLOWS_STORE_ID.replace("{storeId}", storeId)) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }.body() // ✅ Cambiado para devolver una lista
    }


    suspend fun createSerialFlow(serialFlowCreateRequest: SerialFlowCreateRequest): List<SerialFlow> {
        return client.post(ApiConstants.Accounting.SERIAL_FLOWS) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            contentType(ContentType.Application.Json)
            setBody(serialFlowCreateRequest)
        }.body() // ✅ Verifica que el backend realmente devuelve una lista
    }

    suspend fun deleteSerialFlow(seriesGroup: Int) {
        client.delete(ApiConstants.Accounting.SERIAL_FLOWS_DELETE.replace("{seriesGroup}", seriesGroup.toString())) {
            _authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
    }


    // *************************** CONTABILIDAD FIN APIS ***************************

}*/