# set count to 0
# for each line in file
#   set number to 0
#   for each char in line
#     if char is num
#       add char to number
#   add number to count
# give back count

@main = num
 : argc = num, argv = [str]
 ? argc >= 0, argv != 0
{
    $input = ifstream(argv[0]);
    if (!input) # file does not exist
        give -1;

    $count = 0;

    for ($line = input.readLine(); line; line = input.readLine())
    {
        $isfirst = 1;
        $first   = line.get(0);
        $last    = line.get(0);
        for ($j = 0; j < line.size(); j++)
        {
            $char = line.get(j);
            if (isdigit(char))
                if (isfirst)
                {
                    isfirst = 0;
                    first = last = char;
                }
                else last = char;
        }
        count += num(first + last);
    }

    give count;
}

@finddigit = num
 : string = str
{
    if (string.contains("one")) give 1;
    if (string.contains("two")) give 2;
    if (string.contains("three")) give 3;
    if (string.contains("four")) give 4;
    if (string.contains("five")) give 5;
    if (string.contains("six")) give 6;
    if (string.contains("seven")) give 7;
    if (string.contains("eight")) give 8;
    if (string.contains("nine")) give 9;
    give 0;
}