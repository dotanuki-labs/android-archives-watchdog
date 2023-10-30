#!/usr/bin/env bats

setup() {
    current_dir="$(cd "$(dirname "$BATS_TEST_FILENAME")" >/dev/null 2>&1 && pwd)"
    test_root="$(cd "${current_dir%/*}" >/dev/null 2>&1 && pwd)"
    project_root="$(cd "${test_root%/*}" >/dev/null 2>&1 && pwd)"
    run mkdir -p "$project_root/.tmp/"
}

teardown() {
    run rm -rf "$project_root/.tmp/"
}

e2e() {
    local repo="$1"
    local version="$2"
    local artifact="$3"
    local baseline="$4"
    local download_url="https://github.com/$repo/releases/download/$version/$artifact"
    run rm -rf "$project_root/.tmp/$artifact"
    run curl -fsSL -o "$project_root.tmp/$artifact" -C - "$download_url"
    run aaw compare -a "$project_root.tmp/$artifact" -b "$test_root/resources/baselines/$baseline"
}

@test "[e2e] duckduckgo" {
    local repo="duckduckgo/Android"
    local version="5.175.1"
    local artifact="duckduckgo-$version-play-release.apk"
    local baseline="scripts/baselines/duckduckgo-$version.toml"

    run e2e "$repo" "$version" "$artifact" "$baseline"

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}

@test "[e2e] woocommerce" {
    local repo="woocommerce/woocommerce-android"
    local version="15.7"
    local artifact="wcandroid-$version.aab"
    local baseline="scripts/baselines/woocommerce-$version.toml"

    run e2e "$repo" "$version" "$artifact" "$baseline"

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}

@test "[e2e] protonmail" {
    local repo="ProtonMail/proton-mail-android"
    local version="3.0.17"
    local artifact="ProtonMail-$version.apk"
    local baseline="scripts/baselines/protonmail-$version.toml"

    run e2e "$repo" "$version" "$artifact" "$baseline"

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}

@test "[e2e] mozilla firefox" {
    local repo="mozilla-mobile/firefox-android"
    local version="118.2.0"
    local release_version="focus-v$version"
    local artifact="focus-$version-arm64-v8a.apk"
    local baseline="scripts/baselines/firefoxfocus-$version.toml"

    run e2e "$repo" "$version" "$artifact" "$baseline"

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}
