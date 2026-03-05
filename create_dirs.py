#!/usr/bin/env python3
import os
import sys

dirs = [
    r'D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\data\model',
    r'D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\data\network',
    r'D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\data\repository',
    r'D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\ui\home',
    r'D:\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\AUTO CUAN\app\src\main\java\com\example\autocuanumkm\ui\navigation'
]

for directory in dirs:
    try:
        os.makedirs(directory, exist_ok=True)
        print(f'✓ Created: {directory}')
    except Exception as e:
        print(f'✗ Failed: {directory} - {e}')
        sys.exit(1)

print('\nAll directories created successfully!')
