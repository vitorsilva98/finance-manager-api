package br.com.finance.manager.api.constants;

public class MethodNamesConstants {

    private MethodNamesConstants() {
        throw new IllegalStateException("Cannot instantiate MethodNamesConstants class");
    }

    /* AuthenticationController */
    public static final String LOGIN = "[POST] /login";

    /* UserController */
    public static final String CREATE_USER = "[POST] /users";
    public static final String GET_USER_BY_ID = "[GET] /users/{id}";
    public static final String GET_ALL_USERS = "[GET] /users";
    public static final String CHANGE_USER_NAME = "[PATCH] /users";
    public static final String UPDATE_USER = "[PUT] /users/{id}";

    /* CategoryController */
    public static final String CREATE_CATEGORY = "[POST] /categories";
    public static final String GET_ALL_CATEGORIES = "[GET] /categories/{id}";
    public static final String GET_CATEGORY_BY_ID = "[GET] /categories";
    public static final String DELETE_CATEGORY_BY_ID = "[DELETE] /categories/{id}";

    /* EntryController */
    public static final String ADD_ENTRY = "[POST] /entries";
    public static final String GET_ENTRY_BY_ID = "[GET] /entries/{id}";
    public static final String GET_ALL_ENTRIES = "[GET] /entries";
    public static final String REVERSE_ENTRY = "[PATCH] /entries/{id}";
    public static final String DELETE_ENTRY_BY_ID = "[DELETE] /entries/{id}";
}
