package com.orgname.qa.lib.cucumber;

import io.cucumber.plugin.event.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TestCaseTags {

    private static final String PREFIX = "@";

    private final List<String> tags;

    public TestCaseTags() {
        this.tags = new ArrayList<>();
    }

    public TestCaseTags(TestCase testCase) {
        this.tags = testCase.getTags();
    }

    public List<String> filterTagsWithPrefix(String prefix) {
        return this.tags.stream().filter(
                        tag -> tag.startsWith(prefix)
                )
                .collect(
                        Collectors.toList()
                );
    }

    public List<String> filterTagsWithoutPrefixes(Collection<String> prefixes) {
        return this.tags.stream().filter(
                        tag -> prefixes.stream().noneMatch(
                                tag::startsWith
                        )
                )
                .collect(
                        Collectors.toList()
                );
    }

    public String findFirstTagWithPrefix(String prefix) {
        return this.tags.stream().filter(
                        tag -> tag.startsWith(prefix)
                )
                .findFirst()
                .orElse(null);
    }

    public String addTagPrefix(String tag) {
        return String.format("%s%s", PREFIX, tag);
    }

    public List<String> removeTagPrefix(Collection<String> tags) {
        return removeTagPrefix(tags, PREFIX);
    }

    public List<String> removeTagPrefix(Collection<String> tags, String prefix) {
        return tags.stream().map(
                        tag -> removeTagPrefix(tag, prefix)
                )
                .collect(
                        Collectors.toList()
                );
    }

    public String removeTagPrefix(String tag, String prefix) {
        return tag.replace(
                prefix, ""
        );
    }

    public boolean hasTag(String tag) {
        return this.tags.stream().anyMatch(
                t -> t.equalsIgnoreCase(tag)
        );
    }
}

