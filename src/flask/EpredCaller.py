"""
python side wrapper of jms-epred, take arguments and call java side wrapper
"""

import os

AREA_ALL = 'all'
AREA_TOWN = 'town'
AREA_VICE_MODEL = 'vice-model'

DURATION_ALL_YEAR = 'annual'
DURATION_SEMI_YEAR = 'semi-annual'
DURATION_SEASON = 'quarter'

TIME_FIRST_HALF = 0
TIME_SECOND_HALF = 1

TIME_FIRST_SEASON = 1
TIME_SECOND_SEASON = 2
TIME_THIRD_SEASON = 3
TIME_FOURTH_SEASON = 4


class Caller:
    """
    helper class to call the wrapper of epred and fetch its exit status
    """

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

    def predict(self, year, area, duration, timepoint=None):
        """
        invoke the predict functionality of epred
        :param year: specify the year that do the predicition
        :param area: the area of the prediction, can be either AREA_ALL or AREA_TOWN
        :param duration: the duration of the prediction, can be either
                         DURATION_ALL_YEAR, DURATION_SEMI_YEAR or DURATION_SEASON
        :param timepoint: time point of the prediction and the timepoint should match the duration
        :return: the exit status of java wrapper
        """
        self.clear_output_folder()
        args = ['-p-area', area, '-p-year', year, '-p-duration', duration]
        if timepoint:
            args += ['-p-which', timepoint]
        return self._invoke(*args)

    def check_precision(self, year, area, duration, timepoint):
        """
        invoke the check precision functionality of epred
        :param year: the year to check the precision of prediction
        :param area: the area to check the precision, can be either AREA_ALL,
                     AREA_TOWN or AREA_VICE_MODEL
        :param duration: the duration, can be either DURATION_ALL_YEAR,
                         DURATION_SEMI_YEAR or DURATION_SEASON
        :param timepoint: timepoint of the check and this should match the duration
        :return: the exit status of java wrapper
        """
        self.clear_output_folder()
        args = ['-c-area', area, '-c-year', year, '-c-duration', duration]
        if timepoint:
            args += ['-c-which', timepoint]
        return self._invoke(*args)

    def associativity_analysis(self):
        """
        invoke associativity analysis functionality of epred
        :return: the exit status of java wrapper
        """
        self.clear_output_folder()
        return self._invoke("-a")

    def clear_output_folder(self):
        return self._invoke('-rm')

    def _invoke(self, *args):
        ret_status = 0
        if os.fork() == 0:
            args += ('-jar', self._jar_path, '-i', self._input_data_path, '-o',
                     self._output_data_path)
            os.execlp('java', args)
        else:
            child_pid, ret_status = os.wait()
        # todo handle wait return status
        return ret_status & 0x00FF  # lower byte represents the exit status
