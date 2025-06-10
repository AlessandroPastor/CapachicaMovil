package com.example.turismomovile.data.remote.api

object ApiConstants {
    //const val BASE_URL = "http://172.22.8.17:8000"
    const val BASE_URL = "http://192.168.1.9:8000" //PC PASTOR
    //const val BASE_URL = "http://192.172.2.101:8000" //PC JAMIL
    //const val BASE_URL = "http://192.177.1.13:8000" //PC CRISTIAN

    //const val BASE_URL = "AQUI SI SE SUBE ALA NUBE"

    const val REGISTER_ENDPOINT = "$BASE_URL/register"
    const val DETAILS_ENDPOINT = "$BASE_URL/details"

    object Configuration {

        // ROLES
        const val ROLES = "$BASE_URL/role"
        const val ROLE_BY_ID = "$BASE_URL/role/{id}"
        const val ROLE_BY_MODULE = "$BASE_URL/role/module"

        // MODULES FATHER
        const val GET_PARENT_MODULE_BY_ID = "$BASE_URL/parent-module/{id}"
        const val UPDATE_PARENT_MODULE = "$BASE_URL/parent-module/{id}"
        const val DELETE_PARENT_MODULE = "$BASE_URL/parent-module/{id}"
        const val GET_PARENT_MODULE = "$BASE_URL/parent-module"
        const val CREATE_PARENT_MODULE = "$BASE_URL/parent-module"
        const val GET_PARENT_MODULE_LIST = "$BASE_URL/parent-module/list"
        const val GET_PARENT_MODULE_DETAIL_LIST = "$BASE_URL/parent-module/list-detail-module-list"

        // MODULES
        const val MODULES = "$BASE_URL/module"
        const val MODULE_BY_ID = "$BASE_URL/module/{id}"
        const val MODULE_SELECTED = "$BASE_URL/module/modules-selected/roleId/{roleId}/parentModuleId/{parentModuleId}"
        const val MENU_ENDPOINT = "$BASE_URL/module/menu"


        // AUTH
        const val LOGIN_ENDPOINT = "$BASE_URL/login"
        const val REGISTER_ENDPOINT = "$BASE_URL/register"
        const val REFRESH_ENDPOINT = "$BASE_URL/auth/refresh"
        const val LOGOUT_ENDPOINT = "$BASE_URL/auth/logout"
        const val GET_AUTH_VALID = "$BASE_URL/auth/valid"
        const val GET_AUTH_ROLES = "$BASE_URL/auth/roles"

        // MUNICIPALIDAD
        const val MUNICIPALIDAD_ENDPOINT = "$BASE_URL/municipalidad"
        const val MUNICIPALIDAD_GET_BYID = "$BASE_URL/municipalidad/{id}"
        const val MUNICIPALIDAD_POST = "$BASE_URL/municipalidad/crear"
        const val MUNICIPALIDAD_PUT = "$BASE_URL/municipalidad/{id}"
        const val MUNICIPALIDAD_DELETE = "$BASE_URL/municipalidad/{id}"
        const val MUNICIPALIDAD_DESCRIPTION = "$BASE_URL/municipalidad/descripcion"
        const val MUNICIPALIDAD_DESCRIPTIONBYID = "$BASE_URL/municipalidad/descripcion/{id}"

        //SERVICE
        const val SERVICE_ENDPOINT = "$BASE_URL/service"
        const val SERVICE_GET_BYID = "$BASE_URL/service/{id}"
        const val SERVICE_POST = "$BASE_URL/service"
        const val SERVICE_PUT = "$BASE_URL/service/{id}"
        const val SERVICE_DELETE = "$BASE_URL/service/{id}"


        //ASOCIACIONES
        const val ASOCIACION_GET = "$BASE_URL/asociaciones"
        const val ASOCIACION_GETBYID = "$BASE_URL/asociacion/{id}"
        const val ASOCIACION_POST = "$BASE_URL/asociacion"
        const val ASOCIACION_PUT = "$BASE_URL/asociacion/{id}"
        const val ASOCIACION_DELETE = "$BASE_URL/asociacion/{id}"
        const val ASOCIACION_WITH_FAMILY = "$BASE_URL/asociacion/emprendedores/{id}"

        // IMAGENES ASOCIACIONES

        const val IMG_ASOCIACIONES_GET = "$BASE_URL/img-asociacionesTotal"
        const val IMG_ASOCIACIONES_POST = "$BASE_URL/img-asociacion"
        const val IMG_ASOCIACIONES_PUT = "$BASE_URL/img-asociacion/{id}"
        const val IMG_ASOCIACIONES_DELETE = "$BASE_URL/img-asociacion/{id}"
        const val IMG_ASOCIACIONES_GET_BYID = "$BASE_URL/img-asociacion/{id}"
        const val IMG_ASOCIACIONES_GET_ASOCIACIONES = "$BASE_URL/img-asociacion/img/{asociacionId}"



        //EMPRENDEDORES
        const val EMPRENDEDORES_GET = "$BASE_URL/emprendedor"
        const val EMPRENDEDORES_GETBYID = "$BASE_URL/emprendedor/{id}"
        const val EMPRENDEDORES_POST = "$BASE_URL/emprendedor"
        const val EMPRENDEDORES_PUT = "$BASE_URL/emprendedor/{id}"
        const val EMPRENDEDORES_DELETE = "$BASE_URL/emprendedor/{id}"
    }

}
