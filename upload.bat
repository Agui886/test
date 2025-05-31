#!/bin/bash

# 初始化 Git 仓库（如果尚未初始化）
git init

# 添加远程仓库
git remote add origin https://github.com/Agui886/test.git

# 添加文件到暂存区
git add .

# 提交文件
git commit -m "Initial commit or update"

# 推送到远程仓库
git push -u origin master
