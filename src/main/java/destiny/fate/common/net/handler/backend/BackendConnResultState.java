package destiny.fate.common.net.handler.backend;

/**
 * Created by zhangtianlong01 on 2017/10/11.
 */
public interface BackendConnResultState {

    int RESULT_SET_FIELD_COUNT = 0;

    int RESULT_SET_FIELDS = 1;

    int RESULT_SET_EOF = 2;

    int RESULT_SET_ROW = 3;

    int RESULT_SET_LAST_EOF = 4;
}
