import React, {useEffect, useMemo} from "react";
import {RawEventLog} from "../../../data/RawEventLog";
import {createDataSource, DataSource, DataTable, DataTableColumn, Layout, theme} from "flipper-plugin";

export interface RawEventLogState {
  logs: RawEventLog[];
}

type RawLogPageProps = {
  state: RawEventLogState;
  onSelectLog: (log: RawEventLog) => void;
}

export function RawLogView({state, onSelectLog}: RawLogPageProps) {
  const dataSource: DataSource<RawEventLog, string> = useMemo(() => createDataSource<RawEventLog, 'eventId'>([], {key: 'eventId'}), []);

  useEffect(() => {
    state.logs.forEach(log => dataSource.upsert(log));
  }, [state.logs]);

  const onSelect = useMemo(() => (record: RawEventLog) => {
    record && onSelectLog(record);
  }, []);
  const columns: DataTableColumn<RawEventLog>[] = useMemo(() => [
    {
      title: 'id',
      key: 'eventId',
      visible: false,
    },
    {
      title: 'time',
      key: 'time',
    },
    {
      title: 'label',
      key: 'label',
    },
    {
      title: 'payload',
      key: 'payload',
      onRender: log => <>{JSON.stringify(log.payload)}</>
    },
  ], []);

  return (
    <Layout.Container padv={theme.inlinePaddingV} padh={theme.inlinePaddingH} gap={theme.space.medium} grow={true}>
      <DataTable
        columns={columns}
        dataSource={dataSource}
        onSelect={onSelect}
      />
    </Layout.Container>
  );
}