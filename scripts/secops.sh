#! /usr/bin/env bash
# Copyright 2023 Dotanuki Labs
# SPDX-License-Identifier: MIT

# shellcheck disable=SC2129
# shellcheck disable=SC2001

set -e

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly github_token="$1"
readonly git_commit="$2"
readonly sbom="aaw-sbom-$git_commit.json"

install_scanner() {
    local download_url="https://github.com/google/osv-scanner/releases/download/v1.4.2/osv-scanner_1.4.2_linux_amd64"
    curl -fsSL -o "osv-scanner" -C - "$download_url"
    chmod +x ./osv-scanner
}

download_sbom() {
    local download_url="https://api.github.com/repos/dotanuki-labs/android-archives-watchdog/dependency-graph/sbom"
    curl -fsSL \
        -H "Accept: application/vnd.github+json" \
        -H "Authorization: Bearer $github_token" \
        -H "X-GitHub-Api-Version: 2022-11-28" \
        "$download_url" | jq '.sbom' >>"$sbom"
}

analyse_sbom() {
    local scanning
    local five_columns
    local four_columns
    local five_columns="| --- | --- | --- | --- | --- | --- |"
    local four_columns="| --- | --- | --- | --- | --- |"

    scanning=$(./osv-scanner --sbom="$sbom" --format markdown || true)
    no_header=$(echo "$scanning" | sed "s/$five_columns/$four_columns/g" | sed "s/ Source |//g")
    formatted=$(echo "$no_header" | sed "s/ $sbom |//g")

    {
        echo "### CVEs attached to the latest Gradle dependency graph"
        echo "$formatted"
    } >>"$GITHUB_STEP_SUMMARY"
}

echo
echo "🔥 Security analysis related to commit $git_commit"
echo

install_scanner
download_sbom
analyse_sbom

echo
echo "✅ Done"
echo
