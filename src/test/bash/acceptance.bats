#!/usr/bin/env bats

setup() {
    current_dir="$(cd "$(dirname "$BATS_TEST_FILENAME")" >/dev/null 2>&1 && pwd)"
    test_root="$(cd "${current_dir%/*}" >/dev/null 2>&1 && pwd)"
    project_root="$(cd "${test_root%/*}" >/dev/null 2>&1 && pwd)"
    sut="build/bin/aaw"
}

teardown() {
    run rm -rf "io.dotanuki.norris.android.debug.toml"
    run rm -rf "io.dotanuki.norris.android.toml"
}

@test "[general] should show help with no arguments" {
    run "$sut"

    [[ "$output" == *"Usage: aaw <command> [<args>]..."* ]]
    [ "$status" -eq 0 ]
}

@test "[general] should fail with invalid arguments" {
    run "$sut" view

    [[ "$output" == *"Usage: aaw <command> [<args>]..."* ]]
    [ "$status" -eq 1 ]
}

@test "[version] should show app version" {
    version=$(grep "latest" "$project_root/src/main/resources/versions.properties" | sed "s/latest=//g")
    run "$sut" version

    [[ "$output" == *"$version"* ]]
    [ "$status" -eq 0 ]
}

@test "[overview] should fail with invalid archives" {
    run "$sut" overview -a "$test_root/resources/missing.apk"

    [[ "$output" == *"missing.apk does not exist"* ]]
    [ "$status" -eq 69 ]
}

@test "[overview] should compute archive overview" {
    run "$sut" overview --archive="$test_root/resources/app-debug.apk" --json

    [[ $(echo "$output" | jq '.min_sdk') -eq 28 ]]
    [[ $(echo "$output" | jq '.target_sdk') -eq 33 ]]
    [ "$status" -eq 0 ]
}

@test "[baseline] should fail with invalid archives" {
    run "$sut" generate -a "$test_root/resources/missing.apk"

    [[ "$output" == *"missing.apk does not exist"* ]]
    [ "$status" -eq 69 ]
}

@test "[baseline] should generate complete baseline" {
    run "$sut" generate -a "$test_root/resources/app-debug.apk" --verbose

    complete_toml="io.dotanuki.norris.android.debug.toml"
    [[ "$output" == *"Successfully identified artifact type -> APK"* ]]
    [[ "$output" == *"Baseline available at : $complete_toml"* ]]
    [ "$status" -eq 0 ]
}

@test "[baseline] should generate compact baseline" {
    run "$sut" generate -a "$test_root/resources/app-debug.apk" --trust="io.dotanuki"

    compact_toml="io.dotanuki.norris.android.debug.toml"
    [[ "$output" == *"Baseline available at : $compact_toml"* ]]
    [ "$status" -eq 0 ]
}

@test "[comparison] should report no changes between baseline and archive" {
    run "$sut" generate -a "$test_root/resources/app-release.apk"

    toml="io.dotanuki.norris.android.toml"
    run "$sut" compare -a "$test_root/resources/app-release.apk" -b "$toml" --fail

    [[ "$output" == *"No changes detected"* ]]
    [ "$status" -eq 0 ]
}

@test "[comparison] should detect changes between baseline and archive" {
    run "$sut" generate -a "$test_root/resources/app-release.apk" --trust="io.dotanuki"

    toml="io.dotanuki.norris.android.toml"
    run "$sut" compare -a "$test_root/resources/app-release-changed.apk" -b "$toml" --fail

    [[ "$output" == *"Your baseline file does not match the supplied artifact"* ]]
    [ "$status" -eq 69 ]
}
