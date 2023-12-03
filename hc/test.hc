# builtin types:
#  void
#  any
#  num
#  str
#
# declare arrays by using [<type>]
#
# functions:
#@ <return type> <function name>: <argument type> <argument name>, ... ? <precondition>, ...
#{
#    ...
#    give ...;
#}
#
# builtin functions:
#  @ void printf: str fmt, num argc, [any] argv ? fmt != 0, argc >= 0
#

@main = num : argc = num, argv = [str] ? argc >= 0, argv != 0
{
    printf("Hello World!%n", 0);
    give 0;
}
