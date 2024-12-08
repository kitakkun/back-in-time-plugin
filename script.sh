#!/bin/bash

# プロジェクトのルートディレクトリ
PROJECT_ROOT="."  # 必要に応じて変更

# 変更対象のディレクトリパス
OLD_PACKAGE_DIR="io/github/kitakkun"
NEW_PACKAGE_DIR="com/kitakkun"

# 再帰的にディレクトリを検索し、移動
echo "ディレクトリ構造を変更中: $OLD_PACKAGE_DIR -> $NEW_PACKAGE_DIR"
find "$PROJECT_ROOT" -type d -path "*/$OLD_PACKAGE_DIR" | while IFS= read -r old_dir; do
  # 移動先ディレクトリを計算
  new_dir=$(echo "$old_dir" | sed "s|$OLD_PACKAGE_DIR|$NEW_PACKAGE_DIR|")
  
  # 移動先ディレクトリが存在しない場合は作成
  mkdir -p "$(dirname "$new_dir")"
  
  # ディレクトリを移動
  mv "$old_dir" "$new_dir"
  echo "移動: $old_dir -> $new_dir"
done

echo "ディレクトリ構造の変更が完了しました。"
