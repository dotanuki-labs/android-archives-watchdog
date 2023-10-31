#! /usr/bin/env bash

# shellcheck disable=SC2129

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly github_token="$1"
readonly git_commit="$2"
readonly sbom="aaw-sbom-$git_commit.json"

install_bomber() {
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
        -o "$sbom" \
        "$download_url"
}

analyse_sbom() {
    local scanning

    scanning=$(./osv-scanner --sbom="$sbom" --format markdown)

    echo "CVEs attached to the latest Gradle dependency graph" >>"$GITHUB_STEP_SUMMARY"
    echo "" >>"$GITHUB_STEP_SUMMARY"
    echo "$scanning" >>"$GITHUB_STEP_SUMMARY"
}

echo
echo "🔥 Security analysis related to commit $git_commit"
echo

install_bomber
download_sbom
analyse_sbom

echo
echo "✅ Done"
echo
