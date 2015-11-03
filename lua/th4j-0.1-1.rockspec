package = "th4j"
version = "0.1-1"

source = {
    url = "git://github.com/et-chan/th4j/lua",
    tag = "master"
}

description = {
    summary = "th4j",
    detailed = [[]],
    homepage = "https://github.com/et-chan/th4j",
    license = "MIT"
}

dependencies = {
    "torch >= 7.0"
}


build = {
    -- Use command for now
    type = "command",
    build_command = [[
    cmake -E make_directory build;
    cd build;
    cmake .. -DCMAKE_BUILD_TYPE=Release -DCMAKE_PREFIX_PATH="$(LUA_BINDIR)/.." -DCMAKE_INSTALL_PREFIX="$(PREFIX)"
    ]],
    install_command = "cd build && $(MAKE) install"

}