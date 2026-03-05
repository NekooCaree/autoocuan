const fs = require('fs');

const dirs = [
  'D:\\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\AUTO CUAN\\app\\src\\main\\java\\com\\example\\autocuanumkm\\data\\model',
  'D:\\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\AUTO CUAN\\app\\src\\main\\java\\com\\example\\autocuanumkm\\data\\network',
  'D:\\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\AUTO CUAN\\app\\src\\main\\java\\com\\example\\autocuanumkm\\data\\repository',
  'D:\\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\AUTO CUAN\\app\\src\\main\\java\\com\\example\\autocuanumkm\\ui\\home',
  'D:\\VIOLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\AUTO CUAN\\app\\src\\main\\java\\com\\example\\autocuanumkm\\ui\\navigation'
];

dirs.forEach(dir => {
  try {
    fs.mkdirSync(dir, { recursive: true });
    console.log('✓ Created: ' + dir);
  } catch (err) {
    console.error('✗ Failed: ' + dir + ' - ' + err.message);
  }
});

console.log('\nAll directories created successfully!');
