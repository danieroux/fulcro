{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.11";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils } @ inputs:

    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; config.allowUnfree = true; };
        repl = pkgs.writeShellScriptBin "repl" ''
          # Assumes nREPL alias in ~/.clojure/deps.edn
          # https://nrepl.org/nrepl/usage/server.html#using-clojure-cli-tools
          clj -M:dev:tests:nREPL -m nrepl.cmdline
        '';
      in
      {
        formatter = pkgs.nixpkgs-fmt;

        devShells.default = pkgs.mkShellNoCC {
          shellHook = ''
          '';

          packages = [ pkgs.clojure pkgs.gnumake pkgs.yarn pkgs.nodejs repl ];
        };
      });
}
