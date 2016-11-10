"""
define all options for the operations of Epred
all interfaces related to the option of Epred should use constants in this
module as parameters

these constants intend to allow bitwise operation like

AREA_ALL | DURATION_ALL_YEAR | TIME_FIRST_HALF

while not force to use it this way
"""

# area
AREA_ALL = 0x0001
AREA_TOWN = 0x0002
AREA_VICE_MODEL = 0x0003

AREA_MASK = 0x000f

# duration
DURATION_ALL_YEAR = 0x0010
DURATION_SEMI_YEAR = 0x0020
DURATION_SEASON = 0x0030

DURATION_MASK = 0x00f0

# time points of a duration
TIME_FIRST_HALF = 0x0100
TIME_SECOND_HALF = 0x0200

TIME_FIRST_SEASON = 0x0300
TIME_SECOND_SEASON = 0x0400
TIME_THIRD_SEASON = 0x0500
TIME_FOURTH_SEASON = 0x0600

TIME_MASK = 0x0f00

# task
TASK_PREDICT = 0x1000
TASK_PRECISION_CHECK = 0x2000
TASK_ANALYZE = 0x3000

TASK_MASK = 0xf000


class InvalidOption(Exception):
    """
    indicating a invalid option set is passed
    """
    # TODO
    pass


def get_area(option_set):
    """
    extract the area option in the option_set
    """
    return option_set & AREA_MASK


def get_duration(option_set):
    """
    extract the duration option in the option_set
    """
    return option_set & DURATION_MASK


def get_time(option_set):
    """
    extract the time option in the option_set
    """
    return option_set & TIME_MASK

def get_task(option_set):
    """
    extract the task in the option_set
    """
    return option_set & TASK_MASK

def is_valid(option_set):
    """
    whether the option_set is valid, an option_set is invalid iff there are
    conflict or unknown options in the set
    :param option_set: the set of ORing several options
    :return: true if the set is an valid set, false if not
    """
    area = get_area(option_set)
    duration = get_duration(option_set)
    timepoint = get_time(option_set)

    # unknown options
    if area > AREA_VICE_MODEL or duration > DURATION_SEASON or \
                    timepoint > TIME_FOURTH_SEASON:
        return False

    # vice model cannot have other options
    if area == AREA_VICE_MODEL:
        return not duration and not timepoint

    if duration == DURATION_SEMI_YEAR:
        return timepoint == TIME_SECOND_HALF or timepoint == TIME_FIRST_HALF

    # when area is town, cannot choose season as option
    if duration == DURATION_SEASON:
        return area != AREA_TOWN and \
               TIME_FIRST_SEASON <= timepoint <= TIME_FOURTH_SEASON

    # duration == DURATION_ALL_YEAR
    return True

# to be continued :)
