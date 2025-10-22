package pro.thinhha.core;

import org.junit.jupiter.api.Test;
import pro.thinhha.core.util.StringUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for core library utilities.
 */
class CoreLibraryTests {

    @Test
    void testStringUtilEmailValidation() {
        assertTrue(StringUtil.isValidEmail("test@example.com"));
        assertFalse(StringUtil.isValidEmail("invalid-email"));
        assertFalse(StringUtil.isValidEmail(null));
    }

    @Test
    void testStringUtilIsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("test"));
    }

    @Test
    void testStringUtilToSnakeCase() {
        assertEquals("hello_world", StringUtil.toSnakeCase("helloWorld"));
        assertEquals("user_name", StringUtil.toSnakeCase("userName"));
    }
}
