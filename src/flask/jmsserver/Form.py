"""
an abstract of the form pass from the frontend
"""

import jmsserver.EpredOption as EpOp


def _get_option_from_form_value(area_str, duration_str, time_str):
    form_option_map = {
        '请选择': 0,
        '全年': 0,
        '全社会用电量': EpOp.AREA_ALL,
        '分镇街用电量': EpOp.AREA_TOWN,
        '副模型': EpOp.AREA_VICE_MODEL,
        '年度': EpOp.DURATION_ALL_YEAR,
        '半年度': EpOp.DURATION_SEMI_YEAR,
        '季度': EpOp.DURATION_SEASON,
        '上半年': EpOp.TIME_FIRST_HALF,
        '下半年': EpOp.TIME_SECOND_HALF,
        '第一季度': EpOp.TIME_FIRST_SEASON,
        '第二季度': EpOp.TIME_SECOND_SEASON,
        '第三季度': EpOp.TIME_THIRD_SEASON,
        '第四季度': EpOp.TIME_FOURTH_SEASON
    }
    area = form_option_map[area_str]
    duration = form_option_map[duration_str]
    time = form_option_map[time_str]

    return area | duration | time


class Form:
    def __init__(self, form):
        self.form = form

    def can_predict(self):
        """
        :return: whether the form request a predict operation
        """
        return self.form['00'] == 'true'

    def can_check(self):
        """
        :return: whether the form request a check operation
        """
        return self.form['10'] == 'true'

    def can_analyze(self):
        """
        :return: whether the form request a analyze operation
        """
        return self.form['20'] == 'true'

    def predict_option(self):
        """
        :return: a 2-tuple, first element is year, second is the option_set
                 None if the form doesn't request to predict
        """
        options = _get_option_from_form_value(
            self.form['01'], self.form['03'], self.form['04'])
        year = self.form.get('02')

        return year, options

    def check_option(self):
        """
        :return: a 2-tuple, first element is year, second is the option_set
                 None if the form doesn't request to check
        """
        options = _get_option_from_form_value(
            self.form['11'], self.form['13'], self.form['14'])
        year = self.form.get('12')

        return year, options
