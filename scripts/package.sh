#! /usr/bin/env bash

# shellcheck disable=SC2129

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

version=$(grep "latest" src/main/resources/versions.properties | sed "s/latest=//g")

readonly version
readonly fatjar="build/libs/aaw-$version.jar"
readonly output_folder="build/bin"
readonly target_binary="$output_folder/aaw"

if ! test -f "$fatjar"; then
    echo "✗ Error : missing target fatjar!"
    echo
    exit 1
fi

rm -rf "$output_folder"
mkdir -p "$output_folder"
touch "$output_folder/aaw"
echo "#! /usr/bin/env bash" >>"$target_binary"
echo "" >>"$target_binary"
echo "exec java -Xmx1024m -jar \$0 \"\$@\"" >>"$target_binary"
echo "" >>"$target_binary"
echo "" >>"$target_binary"
echo "" >>"$target_binary"

cat "$fatjar" >>"$target_binary"
chmod +x "$target_binary"
