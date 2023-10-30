#!/usr/bin/env bats

setup() {
    current_dir="$(cd "$(dirname "$BATS_TEST_FILENAME")" >/dev/null 2>&1 && pwd)"
    test_root="$(cd "${current_dir%/*}" >/dev/null 2>&1 && pwd)"
    run mkdir -p "$test_root/.tmp"
}

teardown() {
    run rm -rf "$test_root/.tmp"
}

@test "[e2e] duckduckgo" {
    local repo="duckduckgo/Android"
    local version="5.175.1"
    local artifact="duckduckgo-$version-play-release.apk"
    local baseline="$test_root/resources/baselines/duckduckgo-$version.toml"
    local download_url="https://github.com/$repo/releases/download/$version/$artifact"

    run curl -fsSL -o "$test_root/.tmp/$artifact" -C - "$download_url"
    run aaw compare -a "$test_root/.tmp/$artifact" -b "$baseline"

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}

@test "[e2e] woocommerce" {
    local repo="woocommerce/woocommerce-android"
    local version="15.7"
    local artifact="wcandroid-$version.aab"
    local baseline="$test_root/resources/baselines/woocommerce-$version.toml"
    local download_url="https://github.com/$repo/releases/download/$version/$artifact"

    run curl -fsSL -o "$test_root/.tmp/$artifact" -C - "$download_url"
    run aaw compare -a "$test_root/.tmp/$artifact" -b "$baseline"

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}

@test "[e2e] protonmail" {
    local repo="ProtonMail/proton-mail-android"
    local version="3.0.17"
    local artifact="ProtonMail-$version.apk"
    local baseline="$test_root/resources/baselines/protonmail-$version.toml"
    local download_url="https://github.com/$repo/releases/download/$version/$artifact"

    run curl -fsSL -o "$test_root/.tmp/$artifact" -C - "$download_url"
    run aaw compare -a "$test_root/.tmp/$artifact" -b "$baseline"

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}

@test "[e2e] mozilla firefox" {
    local repo="mozilla-mobile/firefox-android"
    local version="118.2.0"
    local release_version="focus-v$version"
    local artifact="focus-$version-arm64-v8a.apk"
    local baseline="$test_root/resources/baselines/firefoxfocus-$version.toml"
    local download_url="https://github.com/$repo/releases/download/$release_version/$artifact"

    run curl -fsSL -o "$test_root/.tmp/$artifact" -C - "$download_url"
    run aaw compare -a "$test_root/.tmp/$artifact" -b "$baseline"

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}
