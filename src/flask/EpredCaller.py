"""
python side wrapper of jms-epred, take arguments and call java side wrapper
"""

import os, EpredOption as EpOp


class Caller:
    """
    helper class to call the wrapper of epred and fetch its exit status
    """

    option_arg_map = {EpOp.AREA_ALL: 'all', EpOp.AREA_TOWN: 'town',
                      EpOp.AREA_VICE_MODEL: 'vice-model',
                      EpOp.DURATION_ALL_YEAR: 'annual',
                      EpOp.DURATION_SEMI_YEAR: 'semi-annual',
                      EpOp.DURATION_SEASON: 'quarter',
                      EpOp.TIME_FIRST_HALF: '0',
                      EpOp.TIME_SECOND_HALF: '1', EpOp.TIME_FIRST_SEASON: '1',
                      EpOp.TIME_SECOND_SEASON: '2', EpOp.TIME_THIRD_SEASON: '3',
                      EpOp.TIME_FOURTH_SEASON: '4', 0: None}

    @classmethod
    def _translate_option(cls, option_set):
        """
        translate the EpredOption to the argument pass to java-wrapper
        not guarentee the validity of the option_set itself
        :param option_set: the set of the option
        :return: the three tuple of argument (area, duration, time)
        """
        area = EpOp.get_area(option_set)
        duration = EpOp.get_duration(option_set)
        time = EpOp.get_time(option_set)

        if not area:
            raise EpOp.InvalidOption("Incomplete Option")

        if not duration and area != EpOp.AREA_VICE_MODEL:
            raise EpOp.InvalidOption("Incomplete Option")

        if not time and duration != EpOp.DURATION_ALL_YEAR:
            raise EpOp.InvalidOption("Incomplete Option")

        return (cls.option_arg_map[area], cls.option_arg_map[duration],
                cls.option_arg_map[time])

    def __init__(self, jar_path, input_data_path, output_data_path):
        """
        constructor of the caller
        :param jar_path: the absolute path to the jar file of the jms wrapper
        :param input_data_path: where the input data is located
        :param output_data_path: where the output file should be located
        """
        self._jar_path = jar_path
        self._input_data_path = input_data_path
        self._output_data_path = output_data_path

        self.clear_output_folder()

    def predict(self, year, option_set):
        """
        invoke the predict functionality of epred
        :param year: specify the year that do the predicition
        :param option_set: the set of options contains area, duration and the time point
        :return: the exit status of java wrapper
        """

        if not EpOp.is_valid(option_set) or EpOp.get_area(
                option_set) == EpOp.AREA_VICE_MODEL:
            raise EpOp.InvalidOption("Invalid Option when predicting")

        area, duration, timepoint = self._translate_option(option_set)

        args = ['-p-area', area, '-p-year', year, '-p-duration', duration]
        if timepoint:
            args += ['-p-which', timepoint]
        return self._invoke(*args)

    def check_precision(self, year, option_set):
        """
        invoke the check precision functionality of epred
        :param year: the year to check the precision of prediction
        :param option_set: the set of options contains area, duration and the time point
        :return: the exit status of java wrapper
        """

        if not EpOp.is_valid(option_set):
            raise EpOp.InvalidOption("Invalid Option when checking precision")

        area, duration, timepoint = self._translate_option(option_set)

        args = ['-c-area', area, '-c-year', year]
        if duration:
            args += ['-c-duration', duration]
            if timepoint:
                args += ['-c-which', timepoint]
        return self._invoke(*args)

    def associativity_analysis(self):
        """
        invoke associativity analysis functionality of epred
        :return: the exit status of java wrapper
        """
        # self.clear_output_folder()
        return self._invoke("-a")

    def clear_output_folder(self):
        return self._invoke('-rm')

    def _invoke(self, *args):
        ret_status = 0
        return os.system('java -jar %s -i %s -o %s ' % (
        self._jar_path, self._input_data_path, self._output_data_path) +
                         ' '.join(args))
