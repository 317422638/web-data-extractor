package im.nll.data.extractor.entity;

import java.util.List;

/**
 * @author <a href="mailto:wuzhiqiang@ucfgroup.com">wuzhiqiang</a>
 * @version Revision: 1.0
 * @date 15/12/30 下午4:07
 */
public interface EntityListExtractor<T> {
    List<T> extractList(String data);
}
